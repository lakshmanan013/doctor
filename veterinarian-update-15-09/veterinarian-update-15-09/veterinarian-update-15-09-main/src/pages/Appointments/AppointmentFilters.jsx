import "./AppointmentPage.css";
const filters = ["All", "In Clinic", "Video", "Home Visit", "Emergency"];

export default function AppointmentFilters() {
  return (
    <div className="appt-filters" style={{ marginBottom: 32 }}>
      {filters.map((item, index) => (
        <button
          key={item}
          className={`appt-filter-btn ${index === 0 ? "active" : ""}`}
        >
          {item}
        </button>
      ))}
    </div>
  );
}
