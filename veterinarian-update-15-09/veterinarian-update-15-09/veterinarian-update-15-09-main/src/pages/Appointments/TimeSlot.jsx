import "./AppointmentPage.css";
function TimeSlot({ time }) {
  const [hour, period] = time.split(" ");

  return (
    <div className="time-slot">
      <div className="panel time-slot-inner">
        <h3 className="time-slot-hour">{hour}</h3>
        <p className="time-slot-period">{period}</p>
      </div>
    </div>
  );
}

export default TimeSlot;
