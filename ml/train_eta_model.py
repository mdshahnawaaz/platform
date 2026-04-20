#!/usr/bin/env python3
"""
Train an ETA prediction model from exported booking history.

Usage:
  python3 ml/train_eta_model.py --input data/eta_training_data.csv --output-dir ml/artifacts
"""

from __future__ import annotations

import argparse
import json
from pathlib import Path

import joblib
import pandas as pd
from sklearn.compose import ColumnTransformer
from sklearn.ensemble import RandomForestRegressor
from sklearn.impute import SimpleImputer
from sklearn.metrics import mean_absolute_error, r2_score
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder


FEATURE_COLUMNS = [
    "hour_of_day",
    "day_of_week",
    "is_weekend",
    "trip_distance_km",
    "driver_to_pickup_distance_km",
    "demand_factor",
    "driver_assigned",
    "live_driver_location_used",
    "vehicle_type",
]
TARGET_COLUMN = "actual_total_duration_minutes"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", required=True, help="CSV exported from /logistics/ai/eta/export.csv")
    parser.add_argument("--output-dir", default="ml/artifacts", help="Where to save the model and metadata")
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    input_path = Path(args.input)
    output_dir = Path(args.output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    df = pd.read_csv(input_path)
    df = df[df["label_ready"] == True].copy()
    if df.empty:
        raise ValueError("No labeled ETA rows found. Complete bookings first, then export again.")

    X = df[FEATURE_COLUMNS]
    y = df[TARGET_COLUMN]

    numeric_features = [
        "hour_of_day",
        "day_of_week",
        "trip_distance_km",
        "driver_to_pickup_distance_km",
        "demand_factor",
    ]
    categorical_features = [
        "is_weekend",
        "driver_assigned",
        "live_driver_location_used",
        "vehicle_type",
    ]

    numeric_transformer = Pipeline(steps=[
        ("imputer", SimpleImputer(strategy="median")),
    ])
    categorical_transformer = Pipeline(steps=[
        ("imputer", SimpleImputer(strategy="most_frequent")),
        ("onehot", OneHotEncoder(handle_unknown="ignore")),
    ])

    preprocessor = ColumnTransformer(
        transformers=[
            ("num", numeric_transformer, numeric_features),
            ("cat", categorical_transformer, categorical_features),
        ]
    )

    model = Pipeline(steps=[
        ("preprocessor", preprocessor),
        ("regressor", RandomForestRegressor(
            n_estimators=300,
            max_depth=14,
            min_samples_leaf=2,
            random_state=42,
        )),
    ])

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42
    )

    model.fit(X_train, y_train)
    predictions = model.predict(X_test)

    metrics = {
        "mae": round(float(mean_absolute_error(y_test, predictions)), 4),
        "r2": round(float(r2_score(y_test, predictions)), 4),
        "training_rows": int(len(X_train)),
        "test_rows": int(len(X_test)),
        "feature_columns": FEATURE_COLUMNS,
        "target_column": TARGET_COLUMN,
        "model_type": "RandomForestRegressor",
    }

    joblib.dump(model, output_dir / "eta_model.joblib")
    (output_dir / "eta_model_metrics.json").write_text(json.dumps(metrics, indent=2))

    print("ETA training complete.")
    print(json.dumps(metrics, indent=2))


if __name__ == "__main__":
    main()
