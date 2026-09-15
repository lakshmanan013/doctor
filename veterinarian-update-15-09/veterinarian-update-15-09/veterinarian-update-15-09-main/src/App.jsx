import { Toaster } from "react-hot-toast";
import AppRoutes from "./routes/AppRoutes";

function App() {
  return (
    <>
      <Toaster position="top-right" toastOptions={{ style: { fontSize: "14px" } }} />
      <AppRoutes />
    </>
  );
}

export default App;
