import { useEffect, useState } from "react";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import { Button } from "@/components/ui/button";

export default function RealTimeDashboard() {
  const [locations, setLocations] = useState([]);

  useEffect(() => {
    const socket = new WebSocket("ws://localhost:4000"); // Replace with your WebSocket URL

    socket.onmessage = (event) => {
      const data = JSON.parse(event.data);
      setLocations((prev) => [...prev, data]); // Ensure data is correctly typed
    };

    return () => {
      socket.close();
    };
  }, []);

  const clearMarkers = () => {
    setLocations([]);
  };

  return (
    <main className="flex min-h-screen items-center justify-center bg-gray-50">
      <MapContainer center={[20.5937, 78.9629]} zoom={5} style={{ height: "100vh", width: "100%" }} >
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        />
        {locations.map((location, index) => (
          <Marker key={index} position={[location.latitude, location.longitude]}>
            <Popup>
              {location.deviceId} <br /> {location.timestamp}
            </Popup>
          </Marker>
        ))}
      </MapContainer>
      <Button onClick={clearMarkers} className="absolute top-4 right-4">Clear Markers</Button>
    </main>
  );
}