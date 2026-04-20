# Demand ML Flow

## Feature columns
- `hour_of_day`
- `day_of_week`
- `is_weekend`
- `bookings_last_15m`
- `bookings_last_30m`
- `bookings_last_60m`
- `active_bookings`
- `available_drivers`
- `avg_estimated_cost_last_60m`
- `avg_trip_distance_last_60m`
- `current_demand_factor`
- `vehicle_type`

## Target
- `target_bookings_next_30m`

This is a regression problem: predict how many bookings are likely to arrive in the next 30 minutes.

## Export training data
Use:

```bash
curl "http://localhost:8081/logistics/ai/feature-snapshots/export.csv?labeledOnly=true" -o data/demand_feature_snapshots.csv
```

## Train the model
Create a virtualenv and install dependencies:

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r ml/requirements.txt
```

Train:

```bash
python3 ml/train_demand_model.py --input data/demand_feature_snapshots.csv --output-dir ml/artifacts
```

## Serve the model
```bash
uvicorn ml.serve_demand_model:app --reload --port 8000
```

## Java integration path
- Java can preview the live payload at `/logistics/ai/model-input`
- Enable external model scoring with:

```properties
ai.model.enabled=true
ai.model.base-url=http://localhost:8000
```

- Then call `/logistics/ai/model-score?vehicleType=standard`

---

# ETA ML Flow

## Feature columns
- `hour_of_day`
- `day_of_week`
- `is_weekend`
- `trip_distance_km`
- `driver_to_pickup_distance_km`
- `demand_factor`
- `driver_assigned`
- `live_driver_location_used`
- `vehicle_type`

## Target
- `actual_total_duration_minutes`

This is a regression problem: predict total ETA from booking creation to delivery completion.

## Export training data
Use:

```bash
curl "http://localhost:8080/logistics/ai/eta/export.csv?completedOnly=true" -o data/eta_training_data.csv
```

## Train the model

```bash
python3 ml/train_eta_model.py --input data/eta_training_data.csv --output-dir ml/artifacts
```

## Serve the model
```bash
uvicorn ml.serve_eta_model:app --reload --port 8001
```

## Java integration path
- Preview ETA training schema at `/logistics/ai/eta/schema`
- Preview a live booking payload at `/logistics/ai/eta/bookings/{bookingId}/model-input`
- Score a booking with the Python ETA model at `/logistics/ai/eta/bookings/{bookingId}/model-score`
- Check ETA model status at `/logistics/ai/eta/model-status`

Enable external ETA scoring with:

```properties
ai.eta.model.enabled=true
ai.eta.model.base-url=http://localhost:8001
```

The app keeps using heuristic ETA automatically when the ETA model is disabled or unavailable.
