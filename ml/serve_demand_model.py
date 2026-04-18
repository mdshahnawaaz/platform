#!/usr/bin/env python3
"""
Minimal FastAPI scorer for the trained demand model.

Run:
  uvicorn ml.serve_demand_model:app --reload --port 8000
"""

from __future__ import annotations

from pathlib import Path

import joblib
import pandas as pd
from fastapi import FastAPI
from pydantic import BaseModel


MODEL_PATH = Path("ml/artifacts/demand_model.joblib")
app = FastAPI(title="Demand Model API")
model = joblib.load(MODEL_PATH)


class DemandModelInput(BaseModel):
    vehicleType: str
    hourOfDay: int
    dayOfWeek: int
    weekend: bool
    bookingsLast15Minutes: int
    bookingsLast30Minutes: int
    bookingsLast60Minutes: int
    activeBookings: int
    availableDrivers: int
    averageEstimatedCostLastHour: float
    averageTripDistanceLastHour: float


@app.get("/health")
def health():
    return {"ok": True, "modelPath": str(MODEL_PATH), "modelLoaded": True}


@app.post("/predict")
def predict(payload: DemandModelInput):
    frame = pd.DataFrame([
        {
            "hour_of_day": payload.hourOfDay,
            "day_of_week": payload.dayOfWeek,
            "is_weekend": payload.weekend,
            "bookings_last_15m": payload.bookingsLast15Minutes,
            "bookings_last_30m": payload.bookingsLast30Minutes,
            "bookings_last_60m": payload.bookingsLast60Minutes,
            "active_bookings": payload.activeBookings,
            "available_drivers": payload.availableDrivers,
            "avg_estimated_cost_last_60m": payload.averageEstimatedCostLastHour,
            "avg_trip_distance_last_60m": payload.averageTripDistanceLastHour,
            "current_demand_factor": 1.0,
            "vehicle_type": payload.vehicleType,
        }
    ])

    predicted = float(model.predict(frame)[0])
    recommended_factor = max(0.85, min(3.0, 1.0 + ((predicted / max(payload.availableDrivers, 1)) * 0.35)))
    return {
        "predictedBookingsNext30Minutes": round(predicted, 2),
        "recommendedDemandFactor": round(recommended_factor, 2),
        "modelSource": "python-random-forest",
    }
