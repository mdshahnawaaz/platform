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
