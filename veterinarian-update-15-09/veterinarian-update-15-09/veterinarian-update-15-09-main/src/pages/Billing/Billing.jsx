import { useEffect, useMemo, useState } from "react";
import { FaWallet, FaClock, FaExclamationCircle, FaReceipt } from "react-icons/fa";
import toast from "react-hot-toast";
import { StatCard } from "../../components/ui/Card";
import Badge from "../../components/ui/Badge";
import InvoiceModal from "../../components/modals/InvoiceModal";
import { getInvoices } from "../../services/billingService";
import { getPatients } from "../../services/patientService";

const statusVariant = { PAID: "success", Paid: "success", PENDING: "warning", Pending: "warning", OVERDUE: "danger", Overdue: "danger", CANCELLED: "danger" };
export default function Billing() {
  const [invoices,setInvoices]=useState([]); const [active,setActive]=useState(null); const [loading,setLoading]=useState(true); const [patientsById,setPatientsById]=useState({});
  const load=async()=>{
    try{
      setLoading(true);
      const [inv,pts]=await Promise.all([getInvoices(),getPatients().catch(()=>[])]);
      const byId={};
      (Array.isArray(pts)?pts:[]).forEach(p=>{byId[String(p.id)]=p;});
      setPatientsById(byId);
      setInvoices(Array.isArray(inv)?inv:[]);
    }catch(e){toast.error(e?.response?.data?.message||"Could not load invoices");}finally{setLoading(false);}
  };
  useEffect(()=>{load();},[]);
  // Backend invoices only carry patientId/ownerId — enrich each row with the
  // patient's name and owner name so the table doesn't just show raw IDs.
  const withNames=(i)=>{
    const p=patientsById[String(i.patientId)];
    return {patientName:p?.name||`Patient #${i.patientId||"—"}`,ownerName:p?.ownerName||`Owner #${i.ownerId||"—"}`,ownerPhone:p?.ownerPhone||"",ownerEmail:p?.ownerEmail||""};
  };
  const stats=useMemo(()=>{const totalBilled=invoices.reduce((s,i)=>s+Number(i.totalAmount||0),0);const collected=invoices.reduce((s,i)=>s+Number(i.paidAmount||0),0);const pending=invoices.reduce((s,i)=>s+Math.max(Number(i.dueAmount||0),0),0);const overdue=invoices.filter(i=>String(i.status).toUpperCase()==="OVERDUE").reduce((s,i)=>s+Number(i.dueAmount||0),0);const avg=invoices.length?Math.round(invoices.reduce((s,i)=>s+Number(i.totalAmount||0),0)/invoices.length):0;return{totalBilled,collected,pending,overdue,avg};},[invoices]);
  const exportCsv=()=>{const rows=[["Invoice","Patient","Owner","Total","Paid","Due","Status","Payment status"],...invoices.map(i=>{const n=withNames(i);return [i.invoiceNumber,n.patientName,n.ownerName,i.totalAmount,i.paidAmount,i.dueAmount,i.status,i.paymentStatus];})];const blob=new Blob([rows.map(r=>r.map(v=>`"${String(v??"").replaceAll('"','""')}"`).join(",")).join("\n")],{type:"text/csv"});const url=URL.createObjectURL(blob);const a=document.createElement("a");a.href=url;a.download="zenve-invoices.csv";a.click();URL.revokeObjectURL(url);};
  if(loading)return <div className="table-card"><div className="table-empty">Loading invoices...</div></div>;
  return <div className="stack-6"><div className="stat-grid"><StatCard icon={FaReceipt} label="Total value" value={`₹${stats.totalBilled.toLocaleString("en-IN")}`} iconBg="info"/><StatCard icon={FaWallet} label="Collected" value={`₹${stats.collected.toLocaleString("en-IN")}`} iconBg="success"/><StatCard icon={FaClock} label="Pending" value={`₹${stats.pending.toLocaleString("en-IN")}`} iconBg="warning"/><StatCard icon={FaExclamationCircle} label="Overdue" value={`₹${stats.overdue.toLocaleString("en-IN")}`} iconBg="danger"/></div><div className="table-card"><table><thead><tr><th>Invoice</th><th>Patient</th><th>Total</th><th>Paid</th><th>Due</th><th>Status</th><th /></tr></thead><tbody>{invoices.map(i=>{const names=withNames(i);return <tr key={i.id} onClick={()=>setActive({...i,...names})} style={{cursor:"pointer"}}><td><p className="cell-title">{i.invoiceNumber||`INV-${i.id}`}</p><p className="cell-sub">{i.invoiceDate||"—"}</p></td><td><p className="cell-title">{names.patientName}</p><p className="cell-sub">{names.ownerName}</p></td><td className="cell-title">₹{Number(i.totalAmount||0).toLocaleString("en-IN")}</td><td className="text-muted">₹{Number(i.paidAmount||0).toLocaleString("en-IN")}</td><td className="text-muted">₹{Number(i.dueAmount||0).toLocaleString("en-IN")}</td><td><Badge variant={statusVariant[i.paymentStatus]||statusVariant[i.status]||"slate"}>{i.paymentStatus||i.status||"—"}</Badge></td><td style={{textAlign:"right"}}>›</td></tr>;})}{!invoices.length&&<tr><td colSpan={7} className="table-empty">No invoices in the backend.</td></tr>}{!!invoices.length&&<tr><td className="cell-title">Total</td><td/><td className="cell-title">₹{stats.totalBilled.toLocaleString("en-IN")}</td><td className="text-muted">₹{stats.collected.toLocaleString("en-IN")}</td><td className="text-muted">₹{stats.pending.toLocaleString("en-IN")}</td><td/><td/></tr>}</tbody></table></div><div style={{display:"flex",justifyContent:"flex-end"}}><button className="link-btn" onClick={exportCsv}>Export GST report</button></div><InvoiceModal open={!!active} invoice={active} onClose={()=>setActive(null)} onUpdated={load}/></div>;
}
