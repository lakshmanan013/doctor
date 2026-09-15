import {
  LayoutDashboard,
  Calendar,
  Users,
  FileText,
  Syringe,
  MessageSquare,
  Package,
  IndianRupee,
  Settings,
} from "lucide-react";

const sidebarItems = [
  { title: "Dashboard", path: "/", icon: LayoutDashboard },
  { title: "Appointments", path: "/appointments", icon: Calendar },
  { title: "Patients", path: "/patients", icon: Users },
  { title: "Prescriptions", path: "/prescriptions", icon: FileText },
  { title: "Vaccinations", path: "/vaccinations", icon: Syringe },
  { title: "Follow-ups", path: "/followups", icon: MessageSquare },
  { title: "Inventory", path: "/inventory", icon: Package },
  { title: "Billing", path: "/billing", icon: IndianRupee },
  { title: "Settings", path: "/settings", icon: Settings },
];

export default sidebarItems;

