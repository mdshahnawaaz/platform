#!/usr/bin/env python3
"""
Minimal FastAPI scorer for the trained ETA model.

Run:
  uvicorn ml.serve_eta_model:app --reload --port 8001
"""

from __future__ import annotations

from pathlib import Path

import joblib
import pandas as pd
from fastapi import FastAPI
from pydantic import BaseModel


MODEL_PATH = Path("ml/artifacts/eta_model.joblib")
app = FastAPI(title="ETA Model API")
model = joblib.load(MODEL_PATH)


class EtaModelInput(BaseModel):
    vehicleType: str
    hourOfDay: int
    dayOfWeek: int
    weekend: bool
    tripDistanceKm: float
    driverToPickupDistanceKm: float | None = None
    demandFactor: float
    driverAssigned: bool
    liveDriverLocationUsed: bool


def heuristic_pickup_minutes(distance_km: float | None, vehicle_type: str, demand_factor: float) -> int | None:
    if distance_km is None:
        return None
    pickup_speed = 28.0 if vehicle_type.lower() == "premium" else 22.0
    effective_speed = max(10.0, pickup_speed / max(demand_factor, 0.92))
    drive_minutes = (distance_km / effective_speed) * 60.0
    return max(1, round(drive_minutes + 3))


@app.get("/health")
def health():
    return {"ok": True, "modelPath": str(MODEL_PATH), "modelLoaded": True}


@app.post("/predict-eta")
def predict(payload: EtaModelInput):
    frame = pd.DataFrame([
        {
            "hour_of_day": payload.hourOfDay,
            "day_of_week": payload.dayOfWeek,
            "is_weekend": payload.weekend,
            "trip_distance_km": payload.tripDistanceKm,
            "driver_to_pickup_distance_km": payload.driverToPickupDistanceKm,
            "demand_factor": payload.demandFactor,
            "driver_assigned": payload.driverAssigned,
            "live_driver_location_used": payload.liveDriverLocationUsed,
            "vehicle_type": payload.vehicleType,
        }
    ])

    predicted_total = max(1, round(float(model.predict(frame)[0])))
    pickup_minutes = heuristic_pickup_minutes(
        payload.driverToPickupDistanceKm,
        payload.vehicleType,
        payload.demandFactor,
    )
    delivery_minutes = max(1, predicted_total - pickup_minutes) if pickup_minutes is not None else predicted_total

    confidence = "HIGH" if payload.liveDriverLocationUsed and payload.tripDistanceKm <= 20 else "MEDIUM"
    if payload.tripDistanceKm > 35:
        confidence = "LOW"

    return {
        "driverArrivalMinutes": pickup_minutes,
        "deliveryMinutes": delivery_minutes,
        "totalEtaMinutes": predicted_total,
        "confidence": confidence,
        "modelSource": "python-random-forest-eta",
    }
