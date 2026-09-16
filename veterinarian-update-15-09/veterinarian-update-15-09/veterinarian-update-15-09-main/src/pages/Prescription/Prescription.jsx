import { useEffect, useRef, useState } from "react";
import toast from "react-hot-toast";
import { FiMic, FiMicOff, FiTrash2, FiDownload, FiSend, FiMail, FiMessageSquare, FiPlusCircle } from "react-icons/fi";
import { FaWhatsapp, FaPaw } from "react-icons/fa";
import jsPDF from "jspdf";
import html2canvas from "html2canvas";
import Input, { Field, Select, Textarea } from "../../components/ui/Input";
import Button from "../../components/ui/Button";
import { getPatients } from "../../services/patientService";
import { getMedicines } from "../../services/medicineService";
import { createPrescription } from "../../services/prescriptionService";
import { createMedicalRecord } from "../../services/medicalRecordService";
import { createVaccination } from "../../services/vaccinationService";
import { sendSms, sendWhatsApp, sendEmail } from "../../services/communicationService";
import { openWhatsApp } from "../../utils/whatsapp";
import { toE164 } from "../../utils/phone";
import useDoctorProfile from "../../hooks/useDoctorProfile";
import "./Prescription.css";

const emptyMed = () => ({ id: Date.now() + Math.random(), medicineId: "", dosage: "", frequency: "Once daily", duration: "", route: "", quantity: "", instructions: "" });
export default function Prescription() {
  const [patients,setPatients]=useState([]);const[allMeds,setAllMeds]=useState([]);const[patientId,setPatientId]=useState("");const[diagnosis,setDiagnosis]=useState("");const[instructions,setInstructions]=useState("");const[notes,setNotes]=useState("");const[petFood,setPetFood]=useState("");const[medicines,setMedicines]=useState([emptyMed()]);const[visitDate,setVisitDate]=useState(new Date().toISOString().slice(0,10));const[complaint,setComplaint]=useState("");const[vitals,setVitals]=useState({temp:"",pulse:"",resp:""});const[saving,setSaving]=useState(false);const[generatingPdf,setGeneratingPdf]=useState(false);
  const[vaccineName,setVaccineName]=useState("");const[vaccineDueDate,setVaccineDueDate]=useState("");
  const[consultationFee,setConsultationFee]=useState("");const[followupFee,setFollowupFee]=useState("");
  const[showFeesOnRx,setShowFeesOnRx]=useState(false);
  const[savedRx,setSavedRx]=useState(null);
  const previewRef=useRef(null);
  const[listening,setListening]=useState(false);
  const recognitionRef=useRef(null);
  const{doctor}=useDoctorProfile();
  // Stop any in-progress dictation if the page unmounts mid-recording.
  useEffect(()=>()=>{recognitionRef.current?.stop();},[]);
  const toggleVoiceCapture=()=>{
    const SpeechRecognition=window.SpeechRecognition||window.webkitSpeechRecognition;
    if(!SpeechRecognition){toast.error("Voice capture isn't supported in this browser. Try Chrome or Edge.");return;}
    if(listening){recognitionRef.current?.stop();return;}
    const recognition=new SpeechRecognition();
    recognition.lang="en-IN";
    recognition.continuous=true;
    recognition.interimResults=true;
    recognition.onstart=()=>setListening(true);
    recognition.onerror=(e)=>{if(e.error!=="no-speech"&&e.error!=="aborted")toast.error(`Voice capture error: ${e.error}`);};
    recognition.onend=()=>{setListening(false);recognitionRef.current=null;};
    recognition.onresult=(event)=>{
      let finalText="";
      for(let i=event.resultIndex;i<event.results.length;i++){
        if(event.results[i].isFinal)finalText+=event.results[i][0].transcript;
      }
      if(finalText.trim()){
        setNotes(prev=>prev?`${prev} ${finalText.trim()}`:finalText.trim());
      }
    };
    recognitionRef.current=recognition;
    try{recognition.start();}catch{/* already starting */}
  };
  useEffect(()=>{Promise.all([getPatients(),getMedicines()]).then(([p,m])=>{setPatients(Array.isArray(p)?p:[]);setAllMeds(Array.isArray(m)?m:[]);}).catch(()=>{});},[]);
  // Auto-fill fees from the doctor's saved profile once it loads, without
  // clobbering anything the doctor has already typed for this visit.
  useEffect(()=>{if(!doctor)return;setConsultationFee(prev=>prev!==""?prev:(doctor.consultationFee??""));setFollowupFee(prev=>prev!==""?prev:(doctor.followUpFee??""));},[doctor]);
  const patient=patients.find(p=>String(p.id)===String(patientId));
  useEffect(()=>{setSavedRx(null);},[patientId]);
  const updateMed=(id,key,value)=>setMedicines(ms=>ms.map(m=>m.id===id?{...m,[key]:value}:m));
  const notesWithPetFood=()=>petFood.trim()?`${notes}${notes.trim()?"\n\n":""}Pet food / diet: ${petFood.trim()}`:notes;
  // Mirrors the backend's own quantity parsing (leading digits, default 1)
  // so the total shown here always matches the invoice that actually gets created.
  const parseQty=(q)=>{const digits=String(q||"").match(/\d+/);return digits?Number(digits[0]):1;};
  const medicineTotal=medicines.reduce((sum,m)=>{const med=allMeds.find(x=>String(x.id)===String(m.medicineId));if(!med||med.price==null)return sum;return sum+Number(med.price)*parseQty(m.quantity);},0);
  const feeTotal=Number(consultationFee||0)+Number(followupFee||0);
  const grandTotal=medicineTotal+feeTotal;
  const submit=async()=>{
    if(savedRx){toast("Prescription is already saved",{icon:"⚠️"});return savedRx;}
    if(!patientId)return toast.error("Select a patient");
    const items=medicines.filter(m=>m.medicineId).map(m=>({medicineId:Number(m.medicineId),dosage:m.dosage,frequency:m.frequency,duration:m.duration,route:m.route,quantity:m.quantity,instructions:m.instructions}));
    try{setSaving(true);const rx=await createPrescription({patientId:Number(patientId),prescriptionDate:visitDate,diagnosis,instructions,notes:notesWithPetFood(),doctorName:doctor?.fullName||"Veterinary Doctor",consultationFee:consultationFee!==""?Number(consultationFee):null,followUpFee:followupFee!==""?Number(followupFee):null,items});
      // Best-effort side records for this visit — a failure here shouldn't
      // stop the prescription itself from being considered saved. Billing
      // (medicines + doctor fees) is handled server-side as a single
      // combined invoice, so there's no separate invoice call here.
      createMedicalRecord({patientId:Number(patientId),visitDate,chiefComplaint:complaint,diagnosis,treatment:instructions,weight:patient?.weight??null,temperature:vitals.temp?Number(vitals.temp):null,notes:notesWithPetFood(),doctorName:doctor?.fullName||"Veterinary Doctor"}).catch(()=>{});
      if(vaccineName.trim()){createVaccination({patientId:Number(patientId),vaccineName:vaccineName.trim(),vaccinationDate:visitDate,nextDueDate:vaccineDueDate||null,administeredBy:doctor?.fullName||"Veterinary Doctor",status:"Completed"}).catch(()=>{});}
      setSavedRx(rx);
      toast.success(`Prescription #${rx.id} saved`);return rx;}catch(e){toast.error(e?.response?.data?.message||"Could not save prescription");throw e;}finally{setSaving(false);}};
  const content=()=>`Patient: ${patient?.name||""}\nDiagnosis: ${diagnosis}\nComplaint: ${complaint}\nMedicines:\n${medicines.map(m=>{const med=allMeds.find(x=>String(x.id)===String(m.medicineId));return `${med?.name||""} ${m.dosage} ${m.frequency} ${m.duration}`;}).join("\n")}\nInstructions: ${instructions}\nPet food / diet: ${petFood||"—"}${vaccineName.trim()?`\nVaccination given: ${vaccineName.trim()}${vaccineDueDate?` (next due ${vaccineDueDate})`:""}`:""}${showFeesOnRx?`\nMedicine charges: ₹${medicineTotal}\nDoctor fee: ₹${feeTotal}\nTotal: ₹${grandTotal}`:""}\nNotes: ${notes}`;
  const downloadPdf=async()=>{
    const node=previewRef.current;
    if(!node)return;
    try{
      setGeneratingPdf(true);

      // Render the live preview at high resolution (min 3x, or the device's own
      // pixel ratio if it's sharper) so the exported PDF looks crisp when printed,
      // not just on-screen.
      const renderScale=Math.max(3,window.devicePixelRatio||1);
      const canvas=await html2canvas(node,{
        scale:renderScale,
        useCORS:true,
        backgroundColor:"#ffffff",
        logging:false,
        windowWidth:node.scrollWidth,
        windowHeight:node.scrollHeight,
      });

      const pdf=new jsPDF({unit:"pt",format:"a4",compress:true});
      const pageWidth=pdf.internal.pageSize.getWidth();
      const pageHeight=pdf.internal.pageSize.getHeight();

      // Real A4 print margin so the prescription doesn't run edge-to-edge.
      const margin=28;
      const contentWidth=pageWidth-margin*2;
      const contentHeight=pageHeight-margin*2;

      // Convert the target content height (in PDF points) into source canvas
      // pixels, then slice the canvas into whole pages so we never cut a
      // medicine row or line of text in half between pages.
      const pxPerPt=canvas.width/contentWidth;
      const pageSlicePx=Math.floor(contentHeight*pxPerPt);
      const totalPages=Math.max(1,Math.ceil(canvas.height/pageSlicePx));

      const pageCanvas=document.createElement("canvas");
      pageCanvas.width=canvas.width;
      const ctx=pageCanvas.getContext("2d");

      for(let page=0;page<totalPages;page++){
        const sliceStart=page*pageSlicePx;
        const sliceHeight=Math.min(pageSlicePx,canvas.height-sliceStart);
        pageCanvas.height=sliceHeight;
        ctx.clearRect(0,0,pageCanvas.width,pageCanvas.height);
        ctx.fillStyle="#ffffff";
        ctx.fillRect(0,0,pageCanvas.width,pageCanvas.height);
        ctx.drawImage(canvas,0,sliceStart,canvas.width,sliceHeight,0,0,canvas.width,sliceHeight);

        const imgData=pageCanvas.toDataURL("image/png",1.0);
        const drawHeight=(sliceHeight*contentWidth)/canvas.width;

        if(page>0)pdf.addPage();
        pdf.addImage(imgData,"PNG",margin,margin,contentWidth,drawHeight,undefined,"FAST");
      }

      const fileName=`prescription-${patient?.name?patient.name.replace(/\s+/g,"-").toLowerCase():"zenve"}-${visitDate}.pdf`;
      pdf.save(fileName);
      toast.success("PDF downloaded");
    }catch(e){
      toast.error("Could not generate PDF");
    }finally{
      setGeneratingPdf(false);
    }
  };
  const patientEmail = (patient?.ownerEmail || patient?.email || patient?.owner?.email || "").trim();

  const getEmailContent = () => {
    const medRows = medicines
      .filter((m) => m.medicineId || m.dosage)
      .map((m, i) => {
        const med = allMeds.find((x) => String(x.id) === String(m.medicineId));
        let line = `${i + 1}. ${med?.name || "Medicine"}`;
        if (m.dosage) line += ` | Dose: ${m.dosage}`;
        if (m.frequency) line += ` | Frequency: ${m.frequency}`;
        if (m.duration) line += ` | Duration: ${m.duration}`;
        if (m.quantity) line += ` | Qty: ${m.quantity}`;
        if (m.instructions) line += `\n   Instructions: ${m.instructions}`;
        return line;
      });

    return [
      `ZENVE VETERINARY CLINIC - PRESCRIPTION`,
      `==================================================`,
      `Doctor: ${doctor?.fullName || "Veterinary Doctor"}`,
      doctor?.qualification ? `Qualification: ${doctor.qualification}` : null,
      doctor?.clinicName ? `Clinic: ${doctor.clinicName}` : null,
      doctor?.phone ? `Doctor Contact: ${doctor.phone}` : null,
      `Date: ${visitDate}`,
      `--------------------------------------------------`,
      `PATIENT DETAILS`,
      `Patient Name: ${patient?.name || "—"}`,
      patient?.petId ? `Pet ID: ${patient.petId}` : null,
      `Species & Breed: ${patient?.species || "—"}${patient?.breed ? ` (${patient.breed})` : ""}`,
      patient?.gender ? `Gender: ${patient.gender}` : null,
      patient?.weight != null ? `Weight: ${patient.weight} kg` : null,
      `Owner: ${patient?.ownerName || "—"}`,
      patientEmail ? `Registered Email: ${patientEmail}` : null,
      patient?.ownerPhone ? `Phone: ${patient.ownerPhone}` : null,
      `--------------------------------------------------`,
      complaint ? `Presenting Complaint:\n${complaint}\n` : null,
      diagnosis ? `Diagnosis:\n${diagnosis}\n` : null,
      `PRESCRIBED MEDICINES:`,
      medRows.length > 0 ? medRows.join("\n\n") : "No medicines prescribed.",
      `--------------------------------------------------`,
      instructions ? `Instructions & Advice:\n${instructions}\n` : null,
      petFood ? `Diet / Pet Food Recommendation:\n${petFood}\n` : null,
      vaccineName.trim() ? `Vaccination Administered: ${vaccineName.trim()}${vaccineDueDate ? ` (Next Due: ${vaccineDueDate})` : ""}\n` : null,
      showFeesOnRx ? `Consultation Fee: ₹${Number(consultationFee || 0).toFixed(2)}\nMedicine Charges: ₹${medicineTotal.toFixed(2)}\nTotal Amount: ₹${grandTotal.toFixed(2)}\n` : null,
      notes ? `Doctor's Notes:\n${notes}\n` : null,
      `==================================================`,
      `This is a computer-generated prescription from Zenve Veterinary Clinic.`,
    ].filter(Boolean).join("\n");
  };

  const send = async (type) => {
    try {
      const rx = await submit();
      if (!rx) return;
      const message = content();
      if (type === "whatsapp") {
        if (!patient?.ownerPhone) return toast.error("This patient has no owner phone number on file");
        const opened = openWhatsApp(patient.ownerPhone, message);
        if (!opened) return toast.error("This patient has no owner phone number on file");
        // Best-effort audit log on the backend — its NOT_CONFIGURED/FAILED
        // status doesn't matter here since wa.me (opened above) is what
        // actually delivers the message, not this call.
        sendWhatsApp({ phoneNumber: patient.ownerPhone, message, type: "PRESCRIPTION", provider: "MANUAL" }).catch(() => {});
        toast.success("WhatsApp opened successfully");
        return;
      }
      let result;
      if (type === "sms") {
        if (!patient?.ownerPhone) return toast.error("This patient has no owner phone number on file");
        result = await sendSms({ phoneNumber: toE164(patient.ownerPhone), message, type: "PRESCRIPTION", provider: "MANUAL" });
      }
      if (type === "email") {
        if (!patientEmail) {
          return toast.error("This patient has no registered email address on file. Please add an email address in the Patient profile.");
        }
        const subject = `Veterinary Prescription: ${patient?.name || "Patient"}${patient?.petId ? ` (${patient.petId})` : ""} - Zenve Veterinary Clinic`;
        const emailBody = getEmailContent();
        result = await sendEmail({
          recipient: patientEmail,
          subject,
          message: emailBody,
          type: "PRESCRIPTION",
          provider: "MANUAL",
        });
      }
      // Reflect what the backend actually did, instead of assuming delivery:
      // a saved "Pending"/"Not configured" log is not the same as a sent message.
      const status = (result?.status || "").toLowerCase();
      const label = type === "sms" ? "SMS" : "Prescription email";
      if (status === "sent") {
        toast.success(type === "email" ? `Prescription sent to registered email (${patientEmail})` : `${label} sent successfully`);
      } else if (status === "failed") {
        toast.error(result?.errorMessage || `Could not send ${type}`);
      } else {
        toast(result?.errorMessage || `${type} was logged but not sent yet — the ${type} provider isn't configured on the server.`, { icon: "⚠️" });
      }
    } catch (e) {
      toast.error(e?.response?.data?.message || `Could not send ${type}`);
    }
  };
  return <div className="rx-layout">
    <div className="panel stack-4">
      <div className="rx-grid-3">
        <Field label="Patient" className="col-span-2"><Select value={patientId} onChange={e=>setPatientId(e.target.value)}><option value="">Select patient</option>{patients.map(p=><option key={p.id} value={p.id}>{p.name} · {p.species} · {p.ownerName||""}</option>)}</Select></Field>
        <Field label="Weight"><Input value={patient?.weight!=null?`${patient.weight} kg`:""} readOnly /></Field>
      </div>
      {patient && (
        <div className="rx-patient-meta-banner">
          <span className="rx-meta-tag">Owner: <strong>{patient.ownerName || "—"}</strong></span>
          <span className="rx-meta-tag">
            Registered Email: {patientEmail ? <strong className="text-email-ok">📧 {patientEmail}</strong> : <span className="text-email-missing">⚠️ No email registered</span>}
          </span>
          {patient.ownerPhone && <span className="rx-meta-tag">Phone: <strong>{patient.ownerPhone}</strong></span>}
        </div>
      )}
      <div className="rx-mic-box">
        <button onClick={toggleVoiceCapture} className={`rx-mic-btn${listening?" rx-mic-btn-active":""}`}>{listening?<FiMicOff size={16}/>:<FiMic size={16}/>}</button>
        <div><p className="rx-mic-title">Voice → Prescription</p><p className="rx-mic-desc">{listening?"Listening... tap the mic again to stop. Speech is added to Notes below.":"Tap the mic and dictate — it's transcribed straight into the Notes field below."}</p></div>
      </div>
      <Field label="Date"><Input type="date" value={visitDate} onChange={e=>setVisitDate(e.target.value)}/></Field>
      <Field label="Presenting complaint"><Input value={complaint} onChange={e=>setComplaint(e.target.value)}/></Field>
      <Field label="Diagnosis"><Input value={diagnosis} onChange={e=>setDiagnosis(e.target.value)}/></Field>
      <div>
        <span className="eyebrow">Medicines</span>
        <div className="stack-3" style={{marginTop:12}}>
          {medicines.map(m=><div key={m.id} className="rx-med-row">
            <Select value={m.medicineId} onChange={e=>updateMed(m.id,"medicineId",e.target.value)}><option value="">Medicine</option>{allMeds.map(x=><option key={x.id} value={x.id}>{x.name} {x.strength||""}</option>)}</Select>
            <Input placeholder="Dose" value={m.dosage} onChange={e=>updateMed(m.id,"dosage",e.target.value)}/>
            <Input placeholder="Frequency" value={m.frequency} onChange={e=>updateMed(m.id,"frequency",e.target.value)}/>
            <Input placeholder="Duration" value={m.duration} onChange={e=>updateMed(m.id,"duration",e.target.value)}/>
            <Input type="number" min="1" placeholder="Qty" value={m.quantity} onChange={e=>updateMed(m.id,"quantity",e.target.value)}/>
            <button onClick={()=>setMedicines(ms=>ms.filter(x=>x.id!==m.id))} className="rx-med-remove"><FiTrash2 size={16}/></button>
          </div>)}
          <button onClick={()=>setMedicines(ms=>[...ms,emptyMed()])} className="rx-chip"><FiPlusCircle size={14}/> Add medicine</button>
        </div>
      </div>
      <div className="rx-grid-4">
        <Field label="Temp (°C)"><Input value={vitals.temp} onChange={e=>setVitals({...vitals,temp:e.target.value})}/></Field>
        <Field label="Pulse"><Input value={vitals.pulse} onChange={e=>setVitals({...vitals,pulse:e.target.value})}/></Field>
        <Field label="Resp"><Input value={vitals.resp} onChange={e=>setVitals({...vitals,resp:e.target.value})}/></Field>
        <Field label="Follow-up days"><Input placeholder="10"/></Field>
      </div>
      <Field label="Instructions"><Input value={instructions} onChange={e=>setInstructions(e.target.value)}/></Field>
      <Field label="Pet food / Diet recommendation"><Input placeholder="e.g. Prescription renal diet, twice daily" value={petFood} onChange={e=>setPetFood(e.target.value)}/></Field>
      <div className="rx-grid-3">
        <Field label="Vaccination given"><Input placeholder="e.g. Rabies" value={vaccineName} onChange={e=>setVaccineName(e.target.value)}/></Field>
        <Field label="Vaccination due date"><Input type="date" value={vaccineDueDate} onChange={e=>setVaccineDueDate(e.target.value)}/></Field>
      </div>
      <div className="rx-grid-3">
        <Field label="Consultation fee (₹)"><Input type="number" min="0" value={consultationFee} onChange={e=>setConsultationFee(e.target.value)}/></Field>
        <Field label="Follow-up fee (₹)"><Input type="number" min="0" value={followupFee} onChange={e=>setFollowupFee(e.target.value)}/></Field>
        <Field label="Slot length (min)"><Input value={doctor?.slotLength??""} readOnly/></Field>
      </div>
      <div className="rx-fee-summary">
        <div className="rx-fee-summary-row"><span>Medicine charges</span><span>₹{medicineTotal.toFixed(2)}</span></div>
        <div className="rx-fee-summary-row"><span>Doctor fee</span><span>₹{feeTotal.toFixed(2)}</span></div>
        <div className="rx-fee-summary-row rx-fee-summary-total"><span>Total</span><span>₹{grandTotal.toFixed(2)}</span></div>
        <button type="button" className="rx-chip" onClick={()=>setShowFeesOnRx(v=>!v)}>
          {showFeesOnRx?"Hide fees on prescription":"Show fees on prescription"}
        </button>
      </div>
      <Field label="Notes"><Textarea rows={3} value={notes} onChange={e=>setNotes(e.target.value)}/></Field>
      <div className="rx-footer">
        <div className="flex-row" style={{gap:8}}>
          <Button variant="secondary" icon={FiDownload} onClick={downloadPdf} disabled={generatingPdf}>{generatingPdf?"Preparing...":"PDF"}</Button>
          <Button variant="secondary" icon={FaWhatsapp} onClick={()=>send("whatsapp")}>WhatsApp</Button>
          <Button variant="secondary" icon={FiMail} onClick={()=>send("email")}>Email</Button>
          <Button variant="secondary" icon={FiMessageSquare} onClick={()=>send("sms")}>SMS</Button>
        </div>
        <Button icon={FiSend} onClick={submit} disabled={saving}>{saving?"Saving...":"Save prescription"}</Button>
      </div>
    </div>

    <div className="rx-preview-wrap">
      <div className="panel rx-preview" ref={previewRef}>

        <div className="rx-preview-header">
          <div className="rx-preview-brand">
            <div className="rx-preview-mark">
              <img src="/zenve.png" alt="Zenve logo" className="rx-preview-logo" />
            </div>
            <div>
              <p className="rx-preview-clinic">Zenve Veterinary Clinic</p>
              <p className="rx-preview-tagline">Veterinary Doctor · General &amp; Emergency Care</p>
            </div>
          </div>
          <div className="rx-preview-doctitle">
            <span className="rx-preview-doc-label">Prescription</span>
            <p className="rx-preview-doc-date">Date: {visitDate}</p>
          </div>
        </div>

        <div className="rx-preview-grid">
          <div><p className="rx-eyebrow">Patient</p><p className="rx-preview-value">{patient?.name||"—"} · {patient?.species||""} · {patient?.breed||""}</p></div>
          <div>
            <p className="rx-eyebrow">Owner</p>
            <p className="rx-preview-value">{patient?.ownerName||"—"}</p>
            {patientEmail && <p style={{ fontSize: "11px", color: "var(--text-muted)", marginTop: "2px" }}>{patientEmail}</p>}
          </div>
          <div><p className="rx-eyebrow">Weight</p><p className="rx-preview-value">{patient?.weight!=null?`${patient.weight} kg`:"—"}</p></div>
          <div><p className="rx-eyebrow">Date</p><p className="rx-preview-value">{visitDate}</p></div>
        </div>

        <div className="rx-preview-body">

          <div className="rx-preview-2col">
            <div className="rx-preview-block">
              <p className="rx-eyebrow"><span className="rx-eyebrow-dot" />Complaint</p>
              <div className="rx-preview-box">{complaint||"—"}</div>
            </div>
            <div className="rx-preview-block">
              <p className="rx-eyebrow"><span className="rx-eyebrow-dot" />Diagnosis</p>
              <div className="rx-preview-box">{diagnosis||"—"}</div>
            </div>
          </div>

          <div className="rx-preview-block">
            <p className="rx-eyebrow"><span className="rx-eyebrow-dot" />Medicines</p>
            {medicines.filter(m=>m.medicineId||m.dosage).length===0 ? (
              <p className="rx-preview-empty">No medicines added yet</p>
            ) : (
              <table className="rx-med-table">
                <thead>
                  <tr>
                    <th>Medicine</th>
                    <th>Duration</th>
                    <th>Frequency</th>
                    <th>Qty</th>
                  </tr>
                </thead>
                <tbody>
                  {medicines.filter(m=>m.medicineId||m.dosage).map((m,i)=>{const med=allMeds.find(x=>String(x.id)===String(m.medicineId));return (
                    <tr key={m.id}>
                      <td>
                        <span className="rx-med-index">{i+1}</span>
                        <span className="rx-med-name">{med?.name||"—"}</span>
                        {m.dosage && <span className="rx-med-dosage">{m.dosage}</span>}
                      </td>
                      <td>{m.duration||"—"}</td>
                      <td>{m.frequency||"—"}</td>
                      <td>{m.quantity||"—"}</td>
                    </tr>
                  );})}
                </tbody>
              </table>
            )}
          </div>

          <div className="rx-preview-2col">
            <div className="rx-preview-block">
              <p className="rx-eyebrow"><span className="rx-eyebrow-dot" />Instructions</p>
              <div className="rx-preview-box">{instructions||"—"}</div>
            </div>
            <div className="rx-preview-block">
              <p className="rx-eyebrow"><span className="rx-eyebrow-dot" />Pet food / Diet</p>
              <div className="rx-preview-box">{petFood||"—"}</div>
            </div>
          </div>

          {vaccineName.trim() && (
            <div className="rx-preview-block">
              <p className="rx-eyebrow"><span className="rx-eyebrow-dot" />Vaccination</p>
              <span className="rx-vaccine-pill">
                <FaPaw /> {vaccineName}{vaccineDueDate?` · Next due ${vaccineDueDate}`:""}
              </span>
            </div>
          )}

          {showFeesOnRx && (
            <div className="rx-preview-block">
              <p className="rx-eyebrow"><span className="rx-eyebrow-dot" />Fees</p>
              <div className="rx-fee-card">
                <div className="rx-fee-card-row"><span>Medicine charges</span><span>₹{medicineTotal.toFixed(2)}</span></div>
                <div className="rx-fee-card-row"><span>Doctor fee</span><span>₹{feeTotal.toFixed(2)}</span></div>
                <div className="rx-fee-card-row rx-fee-card-total"><span>Total</span><span>₹{grandTotal.toFixed(2)}</span></div>
              </div>
            </div>
          )}

          <div className="rx-preview-block">
            <p className="rx-eyebrow"><span className="rx-eyebrow-dot" />Notes</p>
            <div className="rx-preview-box">{notes||"—"}</div>
          </div>

          <div className="rx-preview-footer">
            <p className="rx-preview-footer-note">This is a computer-generated prescription.</p>
            <div className="rx-preview-sign-line">
              <span>Doctor's Signature</span>
            </div>
          </div>

        </div>
      </div>
    </div>
  </div>;
}
