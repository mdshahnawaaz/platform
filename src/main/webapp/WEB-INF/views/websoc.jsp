<!-- <!DOCTYPE html> -->
<html>
<head>
    <title>Driver Tracking</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.0/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
</head>
<body>
    <h1>Driver Location</h1>
    <p>Driver ID: <span id="driverId"></span></p>
    <p>Latitude: <span id="latitude"></span></p>
    <p>Longitude: <span id="longitude"></span></p>

    <script>
        const driverId = "2"; // Get this from your booking
const eventSource = new EventSource(`/track/${driverId}`);

eventSource.onmessage = function(event) {
    const location = JSON.parse(event.data);
    console.log(`Driver ${location.driverId} is at (${location.latitude}, ${location.longitude})`);
    // Update your map or UI here
};

eventSource.onerror = function(error) {
    console.error("EventSource failed:", error);
    eventSource.close();
};
    </script>
</body>
</html>