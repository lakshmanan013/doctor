import { useEffect, useMemo, useState } from "react";
import { FiPlus, FiAlertCircle, FiMinusCircle } from "react-icons/fi";
import { FaFlask, FaBoxOpen, FaCalendarTimes, FaPaperclip } from "react-icons/fa";
import toast from "react-hot-toast";
import { StatCard } from "../../components/ui/Card";
import Badge from "../../components/ui/Badge";
import SearchBar from "../../components/ui/SearchBar";
import BatchModal from "../../components/modals/BatchModal";
import MedicineModal from "../../components/modals/MedicineModal";
import ReduceStockModal from "../../components/modals/ReduceStockModal";
import { getMedicines } from "../../services/medicineService";
import { getBatches, getRecentStockMovements } from "../../services/inventoryService";
import "./Inventory.css";

const TABS = ["All", "Low stock", "Expiring", "Rx only"];
const statusOf = (m, batch) => { if ((m.stockQuantity ?? 0) <= (m.reorderLevel ?? 0)) return "Low stock"; if (batch?.expiryDate && new Date(batch.expiryDate) <= new Date(Date.now() + 60 * 86400000)) return "Expiring soon"; return "In stock"; };
const statusVariant = { "In stock": "success", "Low stock": "warning", "Expiring soon": "warning" };

export default function Inventory() {
  const [medicines, setMedicines] = useState([]); const [batches, setBatches] = useState([]); const [movements, setMovements] = useState([]); const [tab, setTab] = useState("All"); const [query, setQuery] = useState(""); const [selected, setSelected] = useState(null); const [batchTarget, setBatchTarget] = useState(null); const [addMedicineOpen, setAddMedicineOpen] = useState(false); const [reduceTarget, setReduceTarget] = useState(null); const [loading, setLoading] = useState(true);
  const load = async () => { try { setLoading(true); const [m,b,s] = await Promise.all([getMedicines(), getBatches(), getRecentStockMovements().catch(() => [])]); setMedicines(Array.isArray(m) ? m : []); setBatches(Array.isArray(b) ? b : []); setMovements(Array.isArray(s) ? s : []); } catch (e) { toast.error(e?.response?.data?.message || "Could not load inventory"); } finally { setLoading(false); } };
  useEffect(() => { load(); }, []);
  const enriched = useMemo(() => medicines.map((m) => { const bs = batches.filter((b) => b.medicineId === m.id).sort((a,b) => String(a.expiryDate || "").localeCompare(String(b.expiryDate || ""))); const nearest = bs[0]; return { ...m, nearest, status: statusOf(m, nearest) }; }), [medicines,batches]);
  const filtered = enriched.filter((m) => { const q = query.toLowerCase(); const qok = `${m.name} ${m.manufacturer} ${m.category}`.toLowerCase().includes(q); const tok = tab === "All" || (tab === "Low stock" && m.status === "Low stock") || (tab === "Expiring" && m.status === "Expiring soon") || (tab === "Rx only" && (m.dosageForm || "").toLowerCase().includes("prescription")); return qok && tok; });
  const value = medicines.reduce((s,m) => s + Number(m.price || 0) * Number(m.stockQuantity || 0), 0);
  const low = enriched.filter((m) => m.status === "Low stock").length;
  const expiring = enriched.filter((m) => m.status === "Expiring soon").length;
  if (loading) return <div className="table-card"><div className="table-empty">Loading inventory...</div></div>;
  return <div className="stack-6">
    <div className="stat-grid"><StatCard icon={FaFlask} label="SKUs in stock" value={medicines.length} iconBg="primary" /><StatCard icon={FiAlertCircle} label="Low / out of stock" value={low} iconBg="warning" /><StatCard icon={FaCalendarTimes} label="Expiring ≤ 60 days" value={expiring} iconBg="warning" /><StatCard icon={FaPaperclip} label="Inventory value" value={`₹${value.toLocaleString("en-IN")}`} iconBg="success" /></div>
    <div className="table-toolbar"><div className="table-search"><SearchBar value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search medicine, manufacturer, category..." /></div><div className="filter-row">{TABS.map((t) => <button key={t} onClick={() => setTab(t)} className={`filter-chip ${tab === t ? "active" : ""}`}>{t}</button>)}</div><button className="link-action inv-batch-btn" onClick={() => setAddMedicineOpen(true)}><FiPlus size={14} /> Add medicine</button></div>
    <div className="inv-layout"><div className="table-card"><table><thead><tr><th>Medicine</th><th>Category</th><th>Stock</th><th>Nearest expiry</th><th>Status</th><th style={{ textAlign: "right" }}>Action</th></tr></thead><tbody>{filtered.map((m) => <tr key={m.id} onClick={() => setSelected(m)} className={selected?.id === m.id ? "row-selected" : ""}><td><p className="cell-title">{m.name}</p><p className="cell-sub">{m.manufacturer || "—"} · {m.dosageForm || "—"} · {m.strength || ""}</p></td><td className="text-muted">{m.category || "—"}</td><td><span className="cell-title">{m.stockQuantity ?? 0}</span> <span className="cell-sub" style={{ marginTop: 0 }}>{m.unit || "units"}</span></td><td className={m.status !== "In stock" ? "inv-expiry-warn" : "text-muted"}>{m.nearest?.expiryDate || "—"}</td><td><Badge variant={statusVariant[m.status]}>{m.status}</Badge></td><td style={{ textAlign: "right" }}><div style={{ display: "flex", gap: 6, justifyContent: "flex-end" }}><button className="link-action inv-batch-btn" onClick={(e) => { e.stopPropagation(); setBatchTarget(m); }}><FiPlus size={14} /> Batch</button><button className="link-action inv-batch-btn" disabled={!(m.stockQuantity > 0)} onClick={(e) => { e.stopPropagation(); setReduceTarget(m); }}><FiMinusCircle size={14} /> Reduce</button></div></td></tr>)}{!filtered.length && <tr><td colSpan={6} className="table-empty">No medicines found.</td></tr>}</tbody></table></div>
      <div className="stack-4"><div className="panel">{selected ? <><p className="cell-title" style={{ fontSize: 15 }}>{selected.name}</p><p className="cell-sub" style={{ marginBottom: 12 }}>{selected.manufacturer || "—"} · {selected.category || "—"}</p><div className="stack-2"><div className="invoice-line"><span className="text-faint">Stock</span><span style={{ fontWeight: 600 }}>{selected.stockQuantity ?? 0}</span></div><div className="invoice-line"><span className="text-faint">Nearest expiry</span><span style={{ fontWeight: 600 }}>{selected.nearest?.expiryDate || "—"}</span></div><div className="invoice-line"><span className="text-faint">Status</span><Badge variant={statusVariant[selected.status]}>{selected.status}</Badge></div></div></> : <p className="table-empty" style={{ padding: "16px 0" }}>Select a medicine to inspect batches.</p>}</div><div className="inv-alert"><p className="inv-alert-title"><FaBoxOpen size={13} /> Recent stock movements</p>{movements.slice(0,5).map((x) => <p key={x.id} className="inv-alert-desc">{x.movementType} · {x.quantity} · {x.reason || "Stock movement"}</p>)}{!movements.length && <p className="inv-alert-desc">No stock movements yet.</p>}</div></div>
    </div>
    <BatchModal open={!!batchTarget} medicine={batchTarget} onClose={() => setBatchTarget(null)} onSaved={load} />
    <MedicineModal open={addMedicineOpen} onClose={() => setAddMedicineOpen(false)} onSaved={load} />
    <ReduceStockModal open={!!reduceTarget} medicine={reduceTarget} onClose={() => setReduceTarget(null)} onSaved={load} />
  </div>;
}
