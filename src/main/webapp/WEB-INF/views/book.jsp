<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ride Booking System</title>
    <link rel="stylesheet" href="13.css">
    <script src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&callback=initMap" async defer></script>
</head>
<body>

    <!-- Greeting and Name Input -->
    <div class="form-container">
        <h1>Hello!</h1>
        <div id="name-section">
            <label for="nameInput">Enter your name:</label>
            <input type="text" id="nameInput" placeholder="Enter your name" required>
            <button id="nameSubmit">Enter</button>
        </div>

        <!-- Pickup and Drop Location Input (hidden initially) -->
        <div id="location-section" style="display: none;">
            <h2>Hello <span id="userName"></span>!</h2>

            <label for="pickupLat">Enter Pickup Latitude:</label>
            <input type="number" id="pickupLat" step="any" placeholder="Pickup Latitude" required>

            <label for="pickupLong">Enter Pickup Longitude:</label>
            <input type="number" id="pickupLong" step="any" placeholder="Pickup Longitude" required>

            <label for="dropLat">Enter Drop Latitude:</label>
            <input type="number" id="dropLat" step="any" placeholder="Drop Latitude" required>

            <label for="dropLong">Enter Drop Longitude:</label>
            <input type="number" id="dropLong" step="any" placeholder="Drop Longitude" required>

            <label for="vehicleType">Select Vehicle Type:</label>
            <select id="vehicleType">
                <option value="standard">Standard</option>
                <option value="premium">Premium</option>
            </select>

            <button id="calculatePrice">Calculate Price</button>

            <!-- Display estimated price here -->
            <p id="priceDisplay" style="display: none; color: green; font-size: 18px;"></p>
            
            <!-- Button to book after seeing price -->
            <button id="bookRide" style="display: none;">Book Ride</button>
        </div>

        <!-- Driver Assignment Section (hidden initially) -->
        <div id="driver-section" style="display: none;">
            <h3>Driver Assigned!</h3>
            <p>ID: <span id="driverId"></span></p>
            <p>Name: <span id="driverName"></span></p>
            <p>Phone: <span id="driverPhone"></span></p>

            <!-- Map Display -->
            <div id="map" style="height: 400px; width: 100%; display: none;"></div>
            
            <!-- Real-time coordinate display -->
            <div id="driverCoordinates" style="display: none;">
                <h4>Driver's Real-Time Coordinates:</h4>
                <p>Latitude: <span id="driverLat"></span></p>
                <p>Longitude: <span id="driverLong"></span></p>
            </div>
        </div>
    </div>

    <script>

let map;
let driverMarker; // Variable to hold the driver marker

function initMap() {
  // Initialize the map at a default location
  const defaultLocation = { lat: 0, lng: 0 }; // Default location
  map = new google.maps.Map(document.getElementById("map"), {
    zoom: 14,
    center: defaultLocation,
  });

  driverMarker = new google.maps.Marker({
    position: defaultLocation,
    map: map,
    title: "Driver Location",
  });
}

document.getElementById("nameSubmit").addEventListener("click", function () {
  const name = document.getElementById("nameInput").value;

  if (name) {
    // Hide name input section and show location input section
    document.getElementById("name-section").style.display = "none";
    document.getElementById("location-section").style.display = "block";

    // Set the user's name in the greeting
    document.getElementById("userName").innerText = name;
  } else {
    alert("Please enter your name!");
  }
});

document
  .getElementById("calculatePrice")
  .addEventListener("click", function () {
    const pickupLat = document.getElementById("pickupLat").value;
    const pickupLong = document.getElementById("pickupLong").value;
    const dropLat = document.getElementById("dropLat").value;
    const dropLong = document.getElementById("dropLong").value;
    const vehicleType = document.getElementById("vehicleType").value;

    if (pickupLat && pickupLong && dropLat && dropLong) {
      // Calculate the price and display it below the button
      let price = calculatePrice(
        pickupLat,
        pickupLong,
        dropLat,
        dropLong,
        vehicleType
      );

      // Show the price
      const priceDisplay = document.getElementById("priceDisplay");
      priceDisplay.style.display = "block";
    //   priceDisplay.innerText = Estimated Price: $${price};

      // Show the book button
      document.getElementById("bookRide").style.display = "block";
    } else {
      alert("Please enter both pickup and drop coordinates!");
    }
  });

function calculatePrice(pickupLat, pickupLong, dropLat, dropLong, vehicleType) {
  // Simple example: price based on distance and vehicle type
  const distance = Math.sqrt(
    Math.pow(dropLat - pickupLat, 2) + Math.pow(dropLong - pickupLong, 2)
  );
  const baseRate = vehicleType === "premium" ? 3 : 1.5;

  return (distance * baseRate).toFixed(2); // Price estimation formula
}

// Book button event listener
document.getElementById("bookRide").addEventListener("click", function () {
  let confirmBooking = confirm("Do you want to confirm the booking?");

  if (confirmBooking) {
    // Assign the driver and display details
    assignDriver();
  } else {
    // Return to main menu
    returnToMainMenu();
  }
});

function assignDriver() {
  const driverNames = ["John Doe", "Jane Smith", "Mike Johnson", "Emily Davis"];
  const driverPhones = [
    "+123456789",
    "+987654321",
    "+1122334455",
    "+9988776655",
  ];

  // Generate random driver assignment
  const randomIndex = Math.floor(Math.random() * driverNames.length);

  document.getElementById("driverId").innerText = Math.floor(
    1000 + Math.random() * 9000
  ); // Random 4-digit ID
  document.getElementById("driverName").innerText = driverNames[randomIndex];
  document.getElementById("driverPhone").innerText = driverPhones[randomIndex];

  // Show driver section
  document.getElementById("location-section").style.display = "none";
  document.getElementById("driver-section").style.display = "block";

  // Show driver coordinates section
  document.getElementById("driverCoordinates").style.display = "block";
  document.getElementById("map").style.display = "block"; // Show the map

  // Start updating driver's coordinates
  updateDriverCoordinates();
}

function returnToMainMenu() {
  // Hide location section and driver section, reset fields, and show name section
  document.getElementById("location-section").style.display = "none";
  document.getElementById("driver-section").style.display = "none";
  document.getElementById("name-section").style.display = "block";
  document.getElementById("priceDisplay").style.display = "none";
  document.getElementById("bookRide").style.display = "none";
  document.getElementById("nameInput").value = "";
  document.getElementById("pickupLat").value = "";
  document.getElementById("pickupLong").value = "";
  document.getElementById("dropLat").value = "";
  document.getElementById("dropLong").value = "";
}

function updateDriverCoordinates() {
  const driverLat = document.getElementById("driverLat");
  const driverLong = document.getElementById("driverLong");

  // Simulate real-time coordinate updates
  setInterval(() => {
    const newLat = (Math.random() * (90 - -90) + -90).toFixed(6); // Random latitude
    const newLong = (Math.random() * (180 - -180) + -180).toFixed(6); // Random longitude

    // Update the coordinates on the page
    driverLat.innerText = newLat;
    driverLong.innerText = newLong;

    // Update the driver's position on the map
    driverMarker.setPosition(new google.maps.LatLng(newLat, newLong));
    map.setCenter(new google.maps.LatLng(newLat, newLong)); // Center the map on the driver
  }, 5000); // Update every 5 seconds
}
    </script>
</body>
</html>