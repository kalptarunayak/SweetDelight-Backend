/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect } from 'react';
import {
  Cake,
  ShieldCheck,
  CreditCard,
  MapPin,
  Clock,
  Sparkles,
  BookOpen,
  Terminal,
  ExternalLink,
  CheckCircle2,
  AlertTriangle,
  Calendar,
  Gift,
  Copy,
  Check,
  Navigation,
  Bike,
  Compass,
  Phone,
  User,
  Radio,
  Play,
  Pause,
  RotateCcw,
  Crosshair,
  Home,
  CheckCircle,
  Database
} from 'lucide-react';
import SupabaseDataViewer from './components/SupabaseDataViewer';

export default function App() {
  const [activeTab, setActiveTab] = useState<'database' | 'tracking' | 'overview' | 'delivery' | 'payments' | 'rooms' | 'docs'>('database');
  const [copiedSection, setCopiedSection] = useState<string | null>(null);

  // Shop and Delivery Fixed Coordinates (Mumbai, Bandra West)
  const shopLat = 19.0596;
  const shopLng = 72.8295;
  const deliveryRadiusKm = 5.0;
  const customerLat = 19.0620;
  const customerLng = 72.8350;

  // Delivery Radius Interactive Tester
  const [testLat, setTestLat] = useState('19.0620');
  const [testLng, setTestLng] = useState('72.8350');
  const [deliveryResult, setDeliveryResult] = useState<{ distance: number; deliverable: boolean } | null>(null);

  // Room Overlap Tester
  const [bookingDate] = useState('2026-11-20');
  const [testStart, setTestStart] = useState('15:00');
  const [testEnd, setTestEnd] = useState('17:00');
  const [existingStart] = useState('14:00');
  const [existingEnd] = useState('16:00');

  // Coupon Calculator
  const [subtotal, setSubtotal] = useState(1299);
  const [coupon, setCoupon] = useState('SWEET15');

  // Tracking & Mock Map View State
  const [riderLat, setRiderLat] = useState(19.0608);
  const [riderLng, setRiderLng] = useState(72.8318);
  const [isSimulating, setIsSimulating] = useState(false);
  const [simulationStep, setSimulationStep] = useState(2);
  const [selectedOrder] = useState('ORD-20261020-584912');
  const [lastPingTime, setLastPingTime] = useState('Just now');

  // Pre-calculated route coordinates from Shop to Customer
  const routeWaypoints = [
    { label: 'Shop (Bandra West)', lat: 19.0596, lng: 72.8295, speed: '0 km/h' },
    { label: 'Turning onto Waterfield Rd', lat: 19.0601, lng: 72.8306, speed: '24 km/h' },
    { label: 'Passing Turner Rd Junction', lat: 19.0608, lng: 72.8318, speed: '28 km/h' },
    { label: 'Moving along Hill Road', lat: 19.0614, lng: 72.8333, speed: '25 km/h' },
    { label: 'Near St. Peter Church', lat: 19.0618, lng: 72.8344, speed: '18 km/h' },
    { label: 'Customer Doorstep (Sea Green Apts)', lat: 19.0620, lng: 72.8350, speed: '0 km/h' },
  ];

  // Auto-simulation ticker
  useEffect(() => {
    let interval: any;
    if (isSimulating) {
      interval = setInterval(() => {
        setSimulationStep((prev) => {
          const next = (prev + 1) % routeWaypoints.length;
          setRiderLat(routeWaypoints[next].lat);
          setRiderLng(routeWaypoints[next].lng);
          setLastPingTime(new Date().toLocaleTimeString());
          return next;
        });
      }, 2200);
    }
    return () => clearInterval(interval);
  }, [isSimulating, routeWaypoints]);

  const calculateHaversine = (lat1: number, lon1: number, lat2: number, lon2: number) => {
    const R = 6371.0;
    const dLat = ((lat2 - lat1) * Math.PI) / 180;
    const dLon = ((lon2 - lon1) * Math.PI) / 180;
    const a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos((lat1 * Math.PI) / 180) *
        Math.cos((lat2 * Math.PI) / 180) *
        Math.sin(dLon / 2) *
        Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return Math.round(R * c * 100) / 100;
  };

  const handleDeliveryTest = () => {
    const lat = parseFloat(testLat);
    const lng = parseFloat(testLng);
    if (isNaN(lat) || isNaN(lng)) return;
    const dist = calculateHaversine(shopLat, shopLng, lat, lng);
    setDeliveryResult({
      distance: dist,
      deliverable: dist <= deliveryRadiusKm
    });
  };

  const isOverlapping = (s1: string, e1: string, s2: string, e2: string) => {
    return s1 < e2 && e1 > s2;
  };

  const copyToClipboard = (text: string, id: string) => {
    navigator.clipboard.writeText(text);
    setCopiedSection(id);
    setTimeout(() => setCopiedSection(null), 2000);
  };

  const discountAmount = coupon.trim().toUpperCase() === 'SWEET15' ? Math.round(subtotal * 0.15 * 100) / 100 : 0;
  const finalTotal = Math.max(0, subtotal - discountAmount);

  // Map coordinate conversion to percentage on a 800x480 coordinate bounding box
  // Lat range: 19.0580 (bottom: 95%) to 19.0635 (top: 5%)
  // Lng range: 72.8270 (left: 5%) to 72.8370 (right: 95%)
  const minLat = 19.0580;
  const maxLat = 19.0635;
  const minLng = 72.8270;
  const maxLng = 72.8370;

  const getMapX = (lng: number) => {
    const pct = ((lng - minLng) / (maxLng - minLng)) * 100;
    return Math.max(4, Math.min(96, pct));
  };

  const getMapY = (lat: number) => {
    const pct = ((maxLat - lat) / (maxLat - minLat)) * 100;
    return Math.max(4, Math.min(96, pct));
  };

  const remainingDistance = calculateHaversine(riderLat, riderLng, customerLat, customerLng);

  return (
    <div className="min-h-screen bg-neutral-950 text-neutral-100 font-sans selection:bg-rose-500 selection:text-white">
      {/* Top Banner */}
      <header className="border-b border-neutral-800 bg-neutral-900/60 backdrop-blur sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 py-4 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-rose-500 to-amber-500 flex items-center justify-center shadow-lg shadow-rose-500/20">
              <Cake className="w-6 h-6 text-white" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-xl font-bold tracking-tight text-white">SweetDelight API</h1>
                <span className="px-2 py-0.5 text-xs font-semibold rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 flex items-center gap-1">
                  <CheckCircle2 className="w-3 h-3" /> Spring Boot 3.3.4
                </span>
              </div>
              <p className="text-xs text-neutral-400">Artisanal Indian Cake &amp; Chocolate E-Commerce Backend</p>
            </div>
          </div>

          <div className="flex items-center gap-2 flex-wrap">
            <span className="text-xs bg-neutral-800/80 px-3 py-1.5 rounded-lg border border-neutral-700 text-neutral-300 font-mono flex items-center gap-1.5">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
              Port: 8080 (Prod) | Java 17
            </span>
            <a
              href="/swagger-ui.html"
              target="_blank"
              rel="noreferrer"
              className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-rose-600 hover:bg-rose-500 text-white text-xs font-semibold shadow-md shadow-rose-600/30 transition"
            >
              <ExternalLink className="w-3.5 h-3.5" /> Swagger UI
            </a>
          </div>
        </div>

        {/* Tab Navigation */}
        <div className="max-w-7xl mx-auto px-4 sm:px-6 flex gap-2 overflow-x-auto border-t border-neutral-800/60 pt-2">
          {[
            { id: 'database', label: 'Supabase Data Explorer', icon: Database },
            { id: 'tracking', label: 'Live Tracking & Rider Map', icon: Navigation },
            { id: 'overview', label: 'Architecture & Stats', icon: Sparkles },
            { id: 'delivery', label: 'Haversine Radius Check', icon: MapPin },
            { id: 'payments', label: 'Razorpay & Webhooks', icon: CreditCard },
            { id: 'rooms', label: 'Party Room Exclusion', icon: Calendar },
            { id: 'docs', label: 'Deployment & Docs', icon: BookOpen }
          ].map((tab) => {
            const Icon = tab.icon;
            const active = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id as any)}
                className={`flex items-center gap-2 px-3.5 py-2 text-xs font-medium rounded-t-lg transition border-b-2 whitespace-nowrap ${
                  active
                    ? 'border-rose-500 text-white bg-neutral-800/40'
                    : 'border-transparent text-neutral-400 hover:text-neutral-200 hover:bg-neutral-900'
                }`}
              >
                <Icon className={`w-4 h-4 ${active ? 'text-rose-400' : 'text-neutral-400'}`} />
                {tab.label}
              </button>
            );
          })}
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 py-8">

        {/* TAB: SUPABASE DATA EXPLORER */}
        {activeTab === 'database' && (
          <SupabaseDataViewer />
        )}

        {/* TAB: TRACKING & MOCK MAP VIEW */}
        {activeTab === 'tracking' && (
          <div className="space-y-6">
            {/* Order Tracking Header Card */}
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-5 shadow-lg">
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-neutral-800/80 pb-4">
                <div>
                  <div className="flex items-center gap-2.5 flex-wrap">
                    <span className="text-xs font-mono font-semibold px-2.5 py-1 rounded bg-rose-500/10 text-rose-400 border border-rose-500/20">
                      {selectedOrder}
                    </span>
                    <span className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-medium bg-amber-500/10 text-amber-300 border border-amber-500/20">
                      <span className="w-1.5 h-1.5 rounded-full bg-amber-400 animate-ping"></span>
                      OUT_FOR_DELIVERY
                    </span>
                    <span className="text-xs text-neutral-400">
                      ETA: <strong className="text-white">Today 6:30 PM</strong> (~12 mins)
                    </span>
                  </div>
                  <div className="text-xs text-neutral-400 mt-1 flex items-center gap-3">
                    <span>Customer: <strong className="text-neutral-200">Aarav Sharma</strong> (+91 98765 43210)</span>
                    <span>•</span>
                    <span>Item: <strong className="text-neutral-200">Belgian Chocolate Truffle Cake (500g)</strong></span>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <div className="text-right hidden sm:block">
                    <div className="text-xs text-neutral-400">Assigned Delivery Hero</div>
                    <div className="text-xs font-bold text-white flex items-center gap-1.5 justify-end">
                      <Bike className="w-3.5 h-3.5 text-rose-400" /> Vikram Singh (+91 98765 43299)
                    </div>
                  </div>
                  <a
                    href="tel:+919876543299"
                    className="p-2 rounded-lg bg-neutral-800 hover:bg-neutral-700 text-neutral-300 hover:text-white transition border border-neutral-700"
                    title="Call Rider"
                  >
                    <Phone className="w-4 h-4 text-emerald-400" />
                  </a>
                </div>
              </div>

              {/* Status Step Timeline */}
              <div className="grid grid-cols-2 sm:grid-cols-5 gap-2 pt-4">
                {[
                  { status: 'PLACED', time: '17:00', done: true, label: 'Order Placed' },
                  { status: 'CONFIRMED', time: '17:05', done: true, label: 'Confirmed' },
                  { status: 'BAKING', time: '17:20', done: true, label: 'Fresh Baking' },
                  { status: 'OUT_FOR_DELIVERY', time: '18:05', active: true, done: true, label: 'Out for Delivery' },
                  { status: 'DELIVERED', time: 'Pending', done: false, label: 'Delivered' }
                ].map((step, idx) => (
                  <div
                    key={idx}
                    className={`p-2.5 rounded-lg border text-xs transition ${
                      step.active
                        ? 'bg-amber-950/30 border-amber-500/50 text-amber-200'
                        : step.done
                        ? 'bg-neutral-950/60 border-neutral-800 text-neutral-300'
                        : 'bg-neutral-950/20 border-neutral-800/40 text-neutral-600'
                    }`}
                  >
                    <div className="flex items-center justify-between">
                      <span className="font-semibold text-[11px] truncate">{step.label}</span>
                      {step.done ? (
                        <CheckCircle className={`w-3.5 h-3.5 ${step.active ? 'text-amber-400' : 'text-emerald-400'}`} />
                      ) : (
                        <Clock className="w-3.5 h-3.5 text-neutral-600" />
                      )}
                    </div>
                    <div className="text-[10px] text-neutral-400 mt-1">{step.time}</div>
                  </div>
                ))}
              </div>
            </div>

            {/* MOCK MAP CANVAS */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              {/* Map View Box (2 Cols) */}
              <div className="lg:col-span-2 bg-neutral-900 border border-neutral-800 rounded-xl overflow-hidden shadow-xl flex flex-col">
                {/* Map Header Controls */}
                <div className="bg-neutral-950/80 px-4 py-2.5 border-b border-neutral-800 flex items-center justify-between text-xs">
                  <div className="flex items-center gap-2">
                    <Compass className="w-4 h-4 text-rose-400" />
                    <span className="font-semibold text-neutral-200">Live GPS Radar (Bandra West Zone)</span>
                    <span className="px-1.5 py-0.5 rounded bg-emerald-500/10 text-emerald-400 font-mono text-[10px] border border-emerald-500/20">
                      LIVE SYNC
                    </span>
                  </div>
                  <div className="flex items-center gap-3 text-neutral-400 text-[11px]">
                    <span className="flex items-center gap-1 font-mono">
                      <Radio className="w-3 h-3 text-rose-500 animate-pulse" /> Ping: {lastPingTime}
                    </span>
                    <span className="font-mono bg-neutral-900 px-2 py-0.5 rounded border border-neutral-800 text-neutral-300">
                      Lat: {riderLat.toFixed(4)}, Lng: {riderLng.toFixed(4)}
                    </span>
                  </div>
                </div>

                {/* SVG/HTML Tactical Street Map */}
                <div className="relative w-full h-[440px] bg-[#0c1017] overflow-hidden select-none">
                  {/* Subtle Grid and Coastline Texture */}
                  <svg className="absolute inset-0 w-full h-full opacity-40 pointer-events-none">
                    <defs>
                      <pattern id="street-grid" width="40" height="40" patternUnits="userSpaceOnUse">
                        <path d="M 40 0 L 0 0 0 40" fill="none" stroke="#1e293b" strokeWidth="0.8" />
                      </pattern>
                      <linearGradient id="sea-grad" x1="0%" y1="0%" x2="100%" y2="100%">
                        <stop offset="0%" stopColor="#082f49" stopOpacity="0.4" />
                        <stop offset="100%" stopColor="#0284c7" stopOpacity="0.1" />
                      </linearGradient>
                    </defs>
                    {/* Background Grid */}
                    <rect width="100%" height="100%" fill="url(#street-grid)" />

                    {/* Simulated Arabian Sea Coastline curve on Western Edge */}
                    <path
                      d="M 0,0 Q 60,180 30,300 T 0,440 L 0,440 L 0,0 Z"
                      fill="url(#sea-grad)"
                      stroke="#0284c7"
                      strokeWidth="1.5"
                      strokeDasharray="4 2"
                    />
                    <text x="15" y="220" fill="#0284c7" fontSize="10" opacity="0.6" transform="rotate(-90 15,220)">
                      ARABIAN SEA (BANDSTAND)
                    </text>

                    {/* Major Street Lines */}
                    {/* Hill Road */}
                    <path d="M 80,360 C 240,320 480,240 760,160" fill="none" stroke="#334155" strokeWidth="12" strokeLinecap="round" />
                    <path d="M 80,360 C 240,320 480,240 760,160" fill="none" stroke="#64748b" strokeWidth="2" strokeDasharray="6 6" />

                    {/* Turner Road */}
                    <path d="M 200,420 C 320,300 440,160 520,20" fill="none" stroke="#334155" strokeWidth="10" strokeLinecap="round" />
                    <path d="M 200,420 C 320,300 440,160 520,20" fill="none" stroke="#475569" strokeWidth="1.5" strokeDasharray="4 4" />

                    {/* Waterfield Road */}
                    <path d="M 120,80 C 260,140 400,280 480,440" fill="none" stroke="#1e293b" strokeWidth="8" />

                    {/* Active Route Trajectory from Shop to Customer */}
                    <path
                      d={`M ${getMapX(shopLng)}% ${getMapY(shopLat)}% Q ${(getMapX(shopLng) + getMapX(customerLng)) / 2 + 3}% ${(getMapY(shopLat) + getMapY(customerLat)) / 2 + 2}% ${getMapX(customerLng)}% ${getMapY(customerLat)}%`}
                      fill="none"
                      stroke="#e11d48"
                      strokeWidth="4"
                      strokeLinecap="round"
                      opacity="0.3"
                    />

                    {/* Animated Trajectory Dashed Path */}
                    <path
                      d={`M ${getMapX(shopLng)}% ${getMapY(shopLat)}% Q ${(getMapX(shopLng) + getMapX(customerLng)) / 2 + 3}% ${(getMapY(shopLat) + getMapY(customerLat)) / 2 + 2}% ${getMapX(customerLng)}% ${getMapY(customerLat)}%`}
                      fill="none"
                      stroke="#fb7185"
                      strokeWidth="3"
                      strokeDasharray="8 6"
                      strokeLinecap="round"
                    >
                      <animate attributeName="stroke-dashoffset" from="100" to="0" dur="3s" repeatCount="indefinite" />
                    </path>
                  </svg>

                  {/* Street Labels */}
                  <div className="absolute top-[38%] left-[42%] text-[9px] font-mono font-semibold tracking-wider text-slate-400 rotate-[-16deg] pointer-events-none select-none">
                    HILL ROAD
                  </div>
                  <div className="absolute top-[50%] left-[28%] text-[9px] font-mono font-semibold tracking-wider text-slate-500 rotate-[-50deg] pointer-events-none select-none">
                    TURNER ROAD
                  </div>
                  <div className="absolute top-[16%] left-[68%] text-[9px] font-mono font-semibold tracking-wider text-slate-500 rotate-[-12deg] pointer-events-none select-none">
                    LINKING ROAD
                  </div>

                  {/* 1. Shop Marker (SweetDelight Bakery Origin) */}
                  <div
                    className="absolute -translate-x-1/2 -translate-y-1/2 group cursor-pointer z-10"
                    style={{ left: `${getMapX(shopLng)}%`, top: `${getMapY(shopLat)}%` }}
                  >
                    <div className="relative flex items-center justify-center">
                      <div className="w-9 h-9 rounded-full bg-rose-600/30 border border-rose-500/50 flex items-center justify-center shadow-lg shadow-rose-600/30">
                        <div className="w-6 h-6 rounded-full bg-rose-600 flex items-center justify-center text-white font-bold">
                          <Cake className="w-3.5 h-3.5" />
                        </div>
                      </div>
                      <div className="absolute top-10 left-1/2 -translate-x-1/2 bg-neutral-900 border border-neutral-700 px-2 py-0.5 rounded shadow text-[10px] font-semibold text-rose-300 whitespace-nowrap pointer-events-none">
                        Bakery Shop
                      </div>
                    </div>
                  </div>

                  {/* 2. Customer Destination Marker */}
                  <div
                    className="absolute -translate-x-1/2 -translate-y-1/2 group cursor-pointer z-10"
                    style={{ left: `${getMapX(customerLng)}%`, top: `${getMapY(customerLat)}%` }}
                  >
                    <div className="relative flex items-center justify-center">
                      <div className="w-9 h-9 rounded-full bg-emerald-600/30 border border-emerald-500/50 flex items-center justify-center shadow-lg shadow-emerald-600/30">
                        <div className="w-6 h-6 rounded-full bg-emerald-600 flex items-center justify-center text-white font-bold">
                          <Home className="w-3.5 h-3.5" />
                        </div>
                      </div>
                      <div className="absolute top-10 left-1/2 -translate-x-1/2 bg-neutral-900 border border-neutral-700 px-2 py-0.5 rounded shadow text-[10px] font-semibold text-emerald-300 whitespace-nowrap pointer-events-none">
                        Customer Doorstep
                      </div>
                    </div>
                  </div>

                  {/* 3. DYNAMIC RIDER MARKER (Visualizing Latest lat/lng) */}
                  <div
                    className="absolute -translate-x-1/2 -translate-y-1/2 transition-all duration-700 ease-out z-20 cursor-pointer"
                    style={{ left: `${getMapX(riderLng)}%`, top: `${getMapY(riderLat)}%` }}
                  >
                    {/* Multi-layered Pulsing Radar Ring */}
                    <div className="relative flex items-center justify-center">
                      <div className="absolute w-16 h-16 rounded-full bg-amber-500/20 animate-ping"></div>
                      <div className="absolute w-12 h-12 rounded-full bg-amber-400/20 border border-amber-400/40 animate-pulse"></div>
                      <div className="w-8 h-8 rounded-full bg-amber-500 border-2 border-white flex items-center justify-center shadow-xl shadow-amber-500/50">
                        <Bike className="w-4 h-4 text-neutral-950 font-bold" />
                      </div>

                      {/* Live Floating Tooltip */}
                      <div className="absolute bottom-10 left-1/2 -translate-x-1/2 bg-neutral-950/95 border border-amber-500/50 px-2.5 py-1 rounded-lg shadow-xl text-[11px] font-mono text-white whitespace-nowrap flex items-center gap-1.5">
                        <span className="w-2 h-2 rounded-full bg-amber-400 animate-pulse"></span>
                        <span>Rider Vikram ({routeWaypoints[simulationStep]?.speed || '26 km/h'})</span>
                      </div>
                    </div>
                  </div>

                  {/* On-Map HUD Overlay */}
                  <div className="absolute top-3 left-3 bg-neutral-950/90 border border-neutral-800 rounded-lg p-2.5 backdrop-blur text-xs space-y-1 z-10 shadow-lg">
                    <div className="text-[10px] uppercase font-bold text-neutral-400 tracking-wider">Telemetry HUD</div>
                    <div className="flex items-center gap-3 font-mono text-[11px]">
                      <div>
                        <span className="text-neutral-500">Remaining:</span>{' '}
                        <span className="text-amber-400 font-bold">{remainingDistance} km</span>
                      </div>
                      <div>
                        <span className="text-neutral-500">Speed:</span>{' '}
                        <span className="text-neutral-200">{routeWaypoints[simulationStep]?.speed || '26 km/h'}</span>
                      </div>
                      <div>
                        <span className="text-neutral-500">GPS Status:</span>{' '}
                        <span className="text-emerald-400 font-bold">Locked (±2m)</span>
                      </div>
                    </div>
                  </div>

                  {/* Map Legend */}
                  <div className="absolute bottom-3 right-3 bg-neutral-950/90 border border-neutral-800 rounded-lg p-2 backdrop-blur text-[10px] text-neutral-300 space-y-1 z-10 font-mono">
                    <div className="flex items-center gap-1.5">
                      <span className="w-2.5 h-2.5 rounded-full bg-rose-500"></span> Bakery (Shop Origin)
                    </div>
                    <div className="flex items-center gap-1.5">
                      <span className="w-2.5 h-2.5 rounded-full bg-amber-400"></span> Rider Latest Position
                    </div>
                    <div className="flex items-center gap-1.5">
                      <span className="w-2.5 h-2.5 rounded-full bg-emerald-500"></span> Destination Address
                    </div>
                  </div>
                </div>

                {/* Map Bottom Bar: Waypoint Presets & Play/Pause Simulation */}
                <div className="bg-neutral-950 px-4 py-3 border-t border-neutral-800 flex flex-wrap items-center justify-between gap-3 text-xs">
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => setIsSimulating(!isSimulating)}
                      className={`px-3 py-1.5 rounded-lg font-semibold flex items-center gap-1.5 transition ${
                        isSimulating
                          ? 'bg-amber-600 hover:bg-amber-500 text-white shadow-md shadow-amber-600/30'
                          : 'bg-emerald-600 hover:bg-emerald-500 text-white shadow-md shadow-emerald-600/30'
                      }`}
                    >
                      {isSimulating ? (
                        <>
                          <Pause className="w-3.5 h-3.5" /> Pause Auto GPS
                        </>
                      ) : (
                        <>
                          <Play className="w-3.5 h-3.5" /> Simulate Live GPS Ping
                        </>
                      )}
                    </button>
                    <button
                      onClick={() => {
                        setIsSimulating(false);
                        setSimulationStep(0);
                        setRiderLat(routeWaypoints[0].lat);
                        setRiderLng(routeWaypoints[0].lng);
                      }}
                      className="px-2.5 py-1.5 bg-neutral-800 hover:bg-neutral-700 text-neutral-300 rounded-lg transition"
                      title="Reset to shop"
                    >
                      <RotateCcw className="w-3.5 h-3.5" />
                    </button>
                  </div>

                  {/* Quick Waypoints */}
                  <div className="flex items-center gap-1.5 overflow-x-auto">
                    <span className="text-[11px] text-neutral-400 font-medium">Waypoints:</span>
                    {routeWaypoints.map((wp, idx) => (
                      <button
                        key={idx}
                        onClick={() => {
                          setIsSimulating(false);
                          setSimulationStep(idx);
                          setRiderLat(wp.lat);
                          setRiderLng(wp.lng);
                          setLastPingTime(new Date().toLocaleTimeString());
                        }}
                        className={`px-2 py-1 rounded text-[11px] font-mono transition ${
                          simulationStep === idx
                            ? 'bg-rose-600 text-white font-bold'
                            : 'bg-neutral-900 hover:bg-neutral-800 text-neutral-400 border border-neutral-800'
                        }`}
                      >
                        P{idx + 1}
                      </button>
                    ))}
                  </div>
                </div>
              </div>

              {/* Rider Coordinate Control & API Verification Panel (1 Col) */}
              <div className="space-y-4">
                {/* Coordinate Manual Adjuster */}
                <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-5 space-y-4">
                  <h3 className="text-sm font-semibold text-white flex items-center gap-2">
                    <Crosshair className="w-4 h-4 text-amber-400" /> Rider GPS Coordinate Controller
                  </h3>
                  <p className="text-xs text-neutral-400">
                    Directly updates the rider's latest location via <code className="text-amber-400">PUT /api/admin/orders/&#123;id&#125;/location</code>.
                  </p>

                  <div className="space-y-3">
                    <div>
                      <div className="flex justify-between text-xs mb-1">
                        <span className="text-neutral-400">Latitude (North):</span>
                        <span className="font-mono text-amber-300 font-bold">{riderLat.toFixed(5)}</span>
                      </div>
                      <input
                        type="range"
                        min="19.0585"
                        max="19.0630"
                        step="0.0001"
                        value={riderLat}
                        onChange={(e) => {
                          setIsSimulating(false);
                          setRiderLat(parseFloat(e.target.value));
                          setLastPingTime('Just now');
                        }}
                        className="w-full accent-amber-500 cursor-pointer"
                      />
                    </div>

                    <div>
                      <div className="flex justify-between text-xs mb-1">
                        <span className="text-neutral-400">Longitude (East):</span>
                        <span className="font-mono text-amber-300 font-bold">{riderLng.toFixed(5)}</span>
                      </div>
                      <input
                        type="range"
                        min="72.8280"
                        max="72.8360"
                        step="0.0001"
                        value={riderLng}
                        onChange={(e) => {
                          setIsSimulating(false);
                          setRiderLng(parseFloat(e.target.value));
                          setLastPingTime('Just now');
                        }}
                        className="w-full accent-amber-500 cursor-pointer"
                      />
                    </div>
                  </div>

                  <div className="p-3 bg-neutral-950 rounded-lg border border-neutral-800 text-xs font-mono space-y-1">
                    <div className="text-neutral-500">// REST API Payload:</div>
                    <div className="text-emerald-400">PUT /api/admin/orders/1/location</div>
                    <div className="text-neutral-300">
                      &#123; "lat": {riderLat.toFixed(4)}, "lng": {riderLng.toFixed(4)} &#125;
                    </div>
                  </div>
                </div>

                {/* Tracking API Endpoint JSON Response Preview */}
                <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-5 space-y-3">
                  <div className="flex items-center justify-between">
                    <h3 className="text-sm font-semibold text-white flex items-center gap-1.5">
                      <Terminal className="w-4 h-4 text-rose-400" /> Tracking Endpoint Response
                    </h3>
                    <span className="text-[10px] font-mono text-neutral-400 bg-neutral-950 px-2 py-0.5 rounded border border-neutral-800">
                      GET /api/orders/1/tracking
                    </span>
                  </div>

                  <pre className="p-3 bg-neutral-950 rounded-lg border border-neutral-800 text-[11px] font-mono text-neutral-300 overflow-x-auto leading-relaxed">
{`{
  "orderId": 1,
  "orderNumber": "${selectedOrder}",
  "status": "OUT_FOR_DELIVERY",
  "estimatedDeliveryTime": "2026-10-20T18:30:00Z",
  "riderName": "Vikram Singh",
  "riderPhone": "+919876543299",
  "riderLat": ${riderLat.toFixed(4)},
  "riderLng": ${riderLng.toFixed(4)},
  "riderLocationUpdatedAt": "${new Date().toISOString()}"
}`}
                  </pre>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* TAB: OVERVIEW */}
        {activeTab === 'overview' && (
          <div className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-4">
                <div className="text-neutral-400 text-xs font-medium uppercase tracking-wider mb-1 flex items-center justify-between">
                  Catalog Seeded
                  <Cake className="w-4 h-4 text-rose-400" />
                </div>
                <div className="text-2xl font-bold text-white">6 Cakes + 6 Chocolates</div>
                <p className="text-xs text-neutral-400 mt-1">Multi-variant pricing (500g, 1kg, gift boxes)</p>
              </div>

              <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-4">
                <div className="text-neutral-400 text-xs font-medium uppercase tracking-wider mb-1 flex items-center justify-between">
                  Delivery Radius
                  <MapPin className="w-4 h-4 text-amber-400" />
                </div>
                <div className="text-2xl font-bold text-white">5.0 km Max</div>
                <p className="text-xs text-neutral-400 mt-1">Haversine formula enforcement &amp; 422 rejection</p>
              </div>

              <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-4">
                <div className="text-neutral-400 text-xs font-medium uppercase tracking-wider mb-1 flex items-center justify-between">
                  Payment Security
                  <CreditCard className="w-4 h-4 text-emerald-400" />
                </div>
                <div className="text-2xl font-bold text-white">Razorpay SDK</div>
                <p className="text-xs text-neutral-400 mt-1">Constant-time HMAC-SHA256 &amp; idempotent webhooks</p>
              </div>

              <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-4">
                <div className="text-neutral-400 text-xs font-medium uppercase tracking-wider mb-1 flex items-center justify-between">
                  Unit Test Suite
                  <ShieldCheck className="w-4 h-4 text-sky-400" />
                </div>
                <div className="text-2xl font-bold text-white">20/20 Passing</div>
                <p className="text-xs text-emerald-400 mt-1">`mvn test` passes cleanly with 0 failures</p>
              </div>
            </div>

            {/* Architecture Highlights */}
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6">
              <h2 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
                <Terminal className="w-5 h-5 text-rose-400" /> Enterprise E-Commerce Specifications
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                <div className="p-4 rounded-lg bg-neutral-950 border border-neutral-800/80">
                  <h3 className="font-semibold text-rose-300 mb-1">1. Server-Side Price &amp; Amount Authority</h3>
                  <p className="text-neutral-400 text-xs leading-relaxed">
                    Client amounts are strictly ignored. The server re-queries each item variant, validates in-stock status, evaluates active campaign coupon codes, computes subtotal and discount, and initializes Razorpay orders with exact paise calculations.
                  </p>
                </div>
                <div className="p-4 rounded-lg bg-neutral-950 border border-neutral-800/80">
                  <h3 className="font-semibold text-amber-300 mb-1">2. Strict Delivery Radius Check</h3>
                  <p className="text-neutral-400 text-xs leading-relaxed">
                    Computes the Great-Circle distance using Haversine formula against shop coordinates. Addresses beyond radius are rejected with HTTP 422 Unprocessable Entity and clear distance breakdown.
                  </p>
                </div>
                <div className="p-4 rounded-lg bg-neutral-950 border border-neutral-800/80">
                  <h3 className="font-semibold text-emerald-300 mb-1">3. Idempotent Webhooks &amp; Verify</h3>
                  <p className="text-neutral-400 text-xs leading-relaxed">
                    Both <code className="text-emerald-400">/api/payments/verify</code> and <code className="text-emerald-400">/api/webhooks/razorpay</code> check the existing payment state. If already PAID, the handler safely skips reprocessing, guaranteeing customers and admins receive only ONE notification.
                  </p>
                </div>
                <div className="p-4 rounded-lg bg-neutral-950 border border-neutral-800/80">
                  <h3 className="font-semibold text-sky-300 mb-1">4. PostgreSQL Exclusion Constraint</h3>
                  <p className="text-neutral-400 text-xs leading-relaxed">
                    Table <code className="text-sky-400">room_bookings</code> is protected at both DB level via <code className="text-neutral-300">EXCLUDE USING gist</code> and application service level to prevent overlapping cake cutting slots for the same celebration room.
                  </p>
                </div>
              </div>
            </div>

            {/* Quick Test Token Helper */}
            <div className="bg-gradient-to-r from-neutral-900 to-neutral-900/80 border border-neutral-800 rounded-xl p-5 flex flex-col md:flex-row items-center justify-between gap-4">
              <div>
                <h3 className="text-sm font-semibold text-white flex items-center gap-1.5">
                  <ShieldCheck className="w-4 h-4 text-emerald-400" /> Swagger Authorize Test Tokens (No Supabase required locally)
                </h3>
                <p className="text-xs text-neutral-400 mt-1">
                  Click Swagger UI, press <span className="text-neutral-200 font-semibold">Authorize</span>, and paste one of these tokens:
                </p>
              </div>
              <div className="flex items-center gap-2 flex-wrap">
                <button
                  onClick={() => copyToClipboard('test-admin-token', 'admin-token')}
                  className="px-3 py-1.5 bg-neutral-800 hover:bg-neutral-700 text-xs rounded-lg border border-neutral-700 text-neutral-200 flex items-center gap-1.5 font-mono transition"
                >
                  {copiedSection === 'admin-token' ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                  test-admin-token (ADMIN)
                </button>
                <button
                  onClick={() => copyToClipboard('test-customer-token', 'cust-token')}
                  className="px-3 py-1.5 bg-neutral-800 hover:bg-neutral-700 text-xs rounded-lg border border-neutral-700 text-neutral-200 flex items-center gap-1.5 font-mono transition"
                >
                  {copiedSection === 'cust-token' ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                  test-customer-token (CUSTOMER)
                </button>
              </div>
            </div>
          </div>
        )}

        {/* TAB: DELIVERY CHECK */}
        {activeTab === 'delivery' && (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6 space-y-4">
              <h2 className="text-base font-semibold text-white flex items-center gap-2">
                <MapPin className="w-5 h-5 text-amber-400" /> Server-Side Haversine Distance Calculator
              </h2>
              <p className="text-xs text-neutral-400">
                Tests the exact algorithm executed in <code className="text-amber-400">DeliveryService.java</code> and validated on every <code className="text-neutral-300">POST /api/orders</code>.
              </p>

              <div className="p-3 bg-neutral-950 rounded-lg border border-neutral-800 text-xs space-y-1">
                <div className="text-neutral-300 font-medium">Bakery Shop Location:</div>
                <div className="text-neutral-400 font-mono">Bandra West, Mumbai (Lat: {shopLat}, Lng: {shopLng})</div>
                <div className="text-neutral-400 font-mono">Delivery Radius: {deliveryRadiusKm} km</div>
              </div>

              <div className="space-y-3">
                <div>
                  <label className="block text-xs font-medium text-neutral-300 mb-1">Customer Delivery Latitude</label>
                  <input
                    type="text"
                    value={testLat}
                    onChange={(e) => setTestLat(e.target.value)}
                    className="w-full bg-neutral-950 border border-neutral-800 rounded-lg px-3 py-2 text-sm text-neutral-100 font-mono focus:outline-none focus:border-rose-500"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-neutral-300 mb-1">Customer Delivery Longitude</label>
                  <input
                    type="text"
                    value={testLng}
                    onChange={(e) => setTestLng(e.target.value)}
                    className="w-full bg-neutral-950 border border-neutral-800 rounded-lg px-3 py-2 text-sm text-neutral-100 font-mono focus:outline-none focus:border-rose-500"
                  />
                </div>

                <div className="flex gap-2 pt-2">
                  <button
                    onClick={handleDeliveryTest}
                    className="flex-1 px-4 py-2 bg-rose-600 hover:bg-rose-500 text-white text-xs font-semibold rounded-lg transition shadow-md shadow-rose-600/30"
                  >
                    Calculate Distance &amp; Check Eligibility
                  </button>
                  <button
                    onClick={() => {
                      setTestLat('19.1363');
                      setTestLng('72.8277');
                    }}
                    className="px-3 py-2 bg-neutral-800 hover:bg-neutral-700 text-xs rounded-lg text-neutral-300"
                    title="Load distant coordinates (Andheri West)"
                  >
                    Test Outside Radius
                  </button>
                </div>
              </div>

              {deliveryResult && (
                <div
                  className={`p-4 rounded-lg border text-xs space-y-1.5 transition ${
                    deliveryResult.deliverable
                      ? 'bg-emerald-950/40 border-emerald-500/40 text-emerald-300'
                      : 'bg-rose-950/40 border-rose-500/40 text-rose-300'
                  }`}
                >
                  <div className="font-bold flex items-center gap-1.5 text-sm">
                    {deliveryResult.deliverable ? (
                      <>
                        <CheckCircle2 className="w-4 h-4 text-emerald-400" /> DELIVERABLE (HTTP 200 OK)
                      </>
                    ) : (
                      <>
                        <AlertTriangle className="w-4 h-4 text-rose-400" /> OUT OF RADIUS (HTTP 422 Unprocessable Entity)
                      </>
                    )}
                  </div>
                  <div>Computed Distance: <span className="font-mono font-bold">{deliveryResult.distance} km</span></div>
                  <div>Store Radius: <span className="font-mono font-bold">{deliveryRadiusKm} km</span></div>
                  {!deliveryResult.deliverable && (
                    <div className="text-rose-400 text-[11px] pt-1">
                      Server rejects order creation with: <code>Delivery address is outside our delivery radius of 5.0 km (distance: {deliveryResult.distance} km)</code>
                    </div>
                  )}
                </div>
              )}
            </div>

            {/* Coupon & Order Recomputation Visualizer */}
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6 space-y-4">
              <h2 className="text-base font-semibold text-white flex items-center gap-2">
                <Gift className="w-5 h-5 text-rose-400" /> Coupon Discount &amp; Order Recomputation
              </h2>
              <p className="text-xs text-neutral-400">
                Orders recompute prices from DB and validate coupon codes within active banner publication windows.
              </p>

              <div className="space-y-3">
                <div>
                  <label className="block text-xs font-medium text-neutral-300 mb-1">Subtotal (INR)</label>
                  <input
                    type="number"
                    value={subtotal}
                    onChange={(e) => setSubtotal(Math.max(0, parseFloat(e.target.value) || 0))}
                    className="w-full bg-neutral-950 border border-neutral-800 rounded-lg px-3 py-2 text-sm text-neutral-100 font-mono focus:outline-none focus:border-rose-500"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-neutral-300 mb-1">Coupon Code</label>
                  <input
                    type="text"
                    value={coupon}
                    onChange={(e) => setCoupon(e.target.value)}
                    placeholder="e.g. SWEET15"
                    className="w-full bg-neutral-950 border border-neutral-800 rounded-lg px-3 py-2 text-sm text-neutral-100 font-mono uppercase focus:outline-none focus:border-rose-500"
                  />
                </div>

                <div className="p-4 bg-neutral-950 rounded-lg border border-neutral-800 text-xs space-y-2">
                  <div className="flex justify-between text-neutral-400">
                    <span>Subtotal:</span>
                    <span className="font-mono text-neutral-200">₹{subtotal.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between text-emerald-400">
                    <span>Discount ({coupon.toUpperCase() === 'SWEET15' ? '15%' : '0%'}):</span>
                    <span className="font-mono">- ₹{discountAmount.toFixed(2)}</span>
                  </div>
                  <div className="border-t border-neutral-800 pt-2 flex justify-between font-bold text-white text-sm">
                    <span>Total Payable:</span>
                    <span className="font-mono text-rose-400">₹{finalTotal.toFixed(2)}</span>
                  </div>
                  <div className="text-[11px] text-neutral-500 font-mono">
                    Razorpay order initialized in paise: {(finalTotal * 100).toFixed(0)} paise
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* TAB: PAYMENTS */}
        {activeTab === 'payments' && (
          <div className="space-y-6">
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6">
              <h2 className="text-base font-semibold text-white mb-2 flex items-center gap-2">
                <CreditCard className="w-5 h-5 text-emerald-400" /> Razorpay Integration Lifecycle
              </h2>
              <p className="text-xs text-neutral-400 mb-6">
                Online orders transition from <span className="text-amber-400 font-mono">PENDING_PAYMENT</span> to <span className="text-emerald-400 font-mono">PLACED</span> only after verified cryptographic signature or webhook confirmation.
              </p>

              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs">
                <div className="p-4 rounded-lg bg-neutral-950 border border-neutral-800 space-y-2">
                  <div className="font-semibold text-rose-400 flex items-center gap-1.5">
                    <span className="w-5 h-5 rounded-full bg-rose-500/20 text-rose-400 flex items-center justify-center font-bold">1</span>
                    Order Initiation
                  </div>
                  <div className="font-mono text-[11px] text-neutral-300">POST /api/orders/&#123;id&#125;/payment</div>
                  <p className="text-neutral-400">
                    Creates or reuses an existing Razorpay order for server-computed total in paise.
                  </p>
                </div>

                <div className="p-4 rounded-lg bg-neutral-950 border border-neutral-800 space-y-2">
                  <div className="font-semibold text-amber-400 flex items-center gap-1.5">
                    <span className="w-5 h-5 rounded-full bg-amber-500/20 text-amber-400 flex items-center justify-center font-bold">2</span>
                    Client Verify or Webhook
                  </div>
                  <div className="font-mono text-[11px] text-neutral-300">POST /api/payments/verify</div>
                  <p className="text-neutral-400">
                    Verifies HMAC-SHA256 signature using constant-time comparison against key secret.
                  </p>
                </div>

                <div className="p-4 rounded-lg bg-neutral-950 border border-neutral-800 space-y-2">
                  <div className="font-semibold text-emerald-400 flex items-center gap-1.5">
                    <span className="w-5 h-5 rounded-full bg-emerald-500/20 text-emerald-400 flex items-center justify-center font-bold">3</span>
                    Status &amp; Notification
                  </div>
                  <div className="font-mono text-[11px] text-neutral-300">Order: PLACED | Payment: PAID</div>
                  <p className="text-neutral-400">
                    Records status event and triggers WhatsApp/Console notification for customer and admin once only.
                  </p>
                </div>
              </div>
            </div>

            {/* Webhook Idempotency Proof */}
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6">
              <h3 className="text-sm font-semibold text-white mb-2 flex items-center gap-2">
                <ShieldCheck className="w-4 h-4 text-emerald-400" /> Tested Idempotency &amp; Signature Verification
              </h3>
              <p className="text-xs text-neutral-400 mb-4">
                Unit test <code className="text-emerald-400">WebhookIdempotencyTest.java</code> verifies that sending the identical Razorpay event twice executes transitions and notifications strictly once:
              </p>
              <div className="p-3 bg-neutral-950 rounded-lg border border-neutral-800 font-mono text-xs text-neutral-300 space-y-1">
                <div>11:32:28.275 [main] INFO WebhookService - Received verified Razorpay webhook event: payment.captured</div>
                <div className="text-emerald-400">11:32:28.283 [main] INFO WebhookService - Order ORD-20261020-001 transitioned to PLACED via webhook</div>
                <div>11:32:28.294 [main] INFO WebhookService - Received verified Razorpay webhook event: payment.captured</div>
                <div className="text-amber-400">11:32:28.295 [main] INFO WebhookService - Payment orde***t123 is already PAID. Webhook handled idempotently.</div>
              </div>
            </div>
          </div>
        )}

        {/* TAB: PARTY ROOMS */}
        {activeTab === 'rooms' && (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6 space-y-4">
              <h2 className="text-base font-semibold text-white flex items-center gap-2">
                <Calendar className="w-5 h-5 text-sky-400" /> Party Room Booking Overlap Engine
              </h2>
              <p className="text-xs text-neutral-400">
                Party rooms are reserved strictly for cake cutting celebrations. PostgreSQL btree_gist exclusion constraints and service logic prevent double booking.
              </p>

              <div className="p-3 bg-neutral-950 rounded-lg border border-neutral-800 text-xs space-y-1">
                <div className="text-neutral-300 font-medium">Existing Confirmed Booking:</div>
                <div className="text-neutral-400 font-mono">Date: {bookingDate}</div>
                <div className="text-neutral-400 font-mono">Slot: {existingStart} - {existingEnd}</div>
                <div className="text-emerald-400 font-mono">Status: APPROVED</div>
              </div>

              <div className="space-y-3">
                <div>
                  <label className="block text-xs font-medium text-neutral-300 mb-1">New Booking Start Time</label>
                  <input
                    type="time"
                    value={testStart}
                    onChange={(e) => setTestStart(e.target.value)}
                    className="w-full bg-neutral-950 border border-neutral-800 rounded-lg px-3 py-2 text-sm text-neutral-100 font-mono focus:outline-none focus:border-rose-500"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-neutral-300 mb-1">New Booking End Time</label>
                  <input
                    type="time"
                    value={testEnd}
                    onChange={(e) => setTestEnd(e.target.value)}
                    className="w-full bg-neutral-950 border border-neutral-800 rounded-lg px-3 py-2 text-sm text-neutral-100 font-mono focus:outline-none focus:border-rose-500"
                  />
                </div>

                <div className="flex gap-2 pt-2">
                  <button
                    onClick={() => {
                      setTestStart('16:00');
                      setTestEnd('18:00');
                    }}
                    className="px-3 py-1.5 bg-neutral-800 hover:bg-neutral-700 text-xs rounded-lg text-neutral-300"
                  >
                    Set Adjacent (16:00-18:00)
                  </button>
                  <button
                    onClick={() => {
                      setTestStart('15:00');
                      setTestEnd('17:00');
                    }}
                    className="px-3 py-1.5 bg-neutral-800 hover:bg-neutral-700 text-xs rounded-lg text-neutral-300"
                  >
                    Set Overlap (15:00-17:00)
                  </button>
                </div>
              </div>

              {(() => {
                const overlap = isOverlapping(existingStart, existingEnd, testStart, testEnd);
                return (
                  <div
                    className={`p-4 rounded-lg border text-xs space-y-1 transition ${
                      overlap
                        ? 'bg-rose-950/40 border-rose-500/40 text-rose-300'
                        : 'bg-emerald-950/40 border-emerald-500/40 text-emerald-300'
                    }`}
                  >
                    <div className="font-bold flex items-center gap-1.5 text-sm">
                      {overlap ? (
                        <>
                          <AlertTriangle className="w-4 h-4 text-rose-400" /> CONFLICT DETECTED (HTTP 409 Conflict)
                        </>
                      ) : (
                        <>
                          <CheckCircle2 className="w-4 h-4 text-emerald-400" /> TIME SLOT AVAILABLE (HTTP 201 Created)
                        </>
                      )}
                    </div>
                    <div>
                      {overlap
                        ? `Requested slot (${testStart} - ${testEnd}) overlaps with existing reservation (${existingStart} - ${existingEnd}). Rejected by database exclusion constraint.`
                        : `Slot (${testStart} - ${testEnd}) does not overlap. Adjacent slots touching at border are allowed.`}
                    </div>
                  </div>
                );
              })()}
            </div>

            <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6 space-y-4">
              <h2 className="text-base font-semibold text-white flex items-center gap-2">
                <ShieldCheck className="w-5 h-5 text-emerald-400" /> PostgreSQL Exclusion Constraint
              </h2>
              <p className="text-xs text-neutral-400">
                Flyway migration <code className="text-emerald-400">V1__initial_schema.sql</code> enables <code className="text-neutral-300">btree_gist</code> and creates the exclusion constraint:
              </p>
              <pre className="p-3 bg-neutral-950 rounded-lg border border-neutral-800 text-[11px] font-mono text-neutral-300 overflow-x-auto">
{`ALTER TABLE room_bookings
    ADD CONSTRAINT no_overlapping_bookings
    EXCLUDE USING gist (
        room_id WITH =,
        booking_date WITH =,
        tsrange((booking_date + start_time)::timestamp,
                (booking_date + end_time)::timestamp) WITH &&
    )
    WHERE (status IN ('PENDING', 'APPROVED'));`}
              </pre>
            </div>
          </div>
        )}

        {/* TAB: DOCS & DEPLOYMENT */}
        {activeTab === 'docs' && (
          <div className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-5 space-y-2">
                <h3 className="font-bold text-white text-sm flex items-center gap-2">
                  <BookOpen className="w-4 h-4 text-rose-400" /> /docs/API.md
                </h3>
                <p className="text-xs text-neutral-400">
                  Full reference listing all 25+ endpoints, sample requests, responses, and 6-line Swagger Authorize guide.
                </p>
                <button
                  onClick={() => copyToClipboard('/docs/API.md', 'api-doc')}
                  className="mt-2 text-xs text-rose-400 hover:text-rose-300 flex items-center gap-1 font-medium"
                >
                  {copiedSection === 'api-doc' ? 'Copied path!' : 'Copy Path: /docs/API.md'}
                </button>
              </div>

              <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-5 space-y-2">
                <h3 className="font-bold text-white text-sm flex items-center gap-2">
                  <Terminal className="w-4 h-4 text-emerald-400" /> /docs/DEPLOY_RENDER.md
                </h3>
                <p className="text-xs text-neutral-400">
                  Step-by-step Render and Supabase setup guide in under 25 lines with exact Dockerfile configuration.
                </p>
                <button
                  onClick={() => copyToClipboard('/docs/DEPLOY_RENDER.md', 'render-doc')}
                  className="mt-2 text-xs text-emerald-400 hover:text-emerald-300 flex items-center gap-1 font-medium"
                >
                  {copiedSection === 'render-doc' ? 'Copied path!' : 'Copy Path: /docs/DEPLOY_RENDER.md'}
                </button>
              </div>

              <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-5 space-y-2">
                <h3 className="font-bold text-white text-sm flex items-center gap-2">
                  <CreditCard className="w-4 h-4 text-amber-400" /> /docs/PAYMENTS.md
                </h3>
                <p className="text-xs text-neutral-400">
                  Concise instructions under 20 lines on Razorpay test keys, webhook registration, and test transactions.
                </p>
                <button
                  onClick={() => copyToClipboard('/docs/PAYMENTS.md', 'pay-doc')}
                  className="mt-2 text-xs text-amber-400 hover:text-amber-300 flex items-center gap-1 font-medium"
                >
                  {copiedSection === 'pay-doc' ? 'Copied path!' : 'Copy Path: /docs/PAYMENTS.md'}
                </button>
              </div>
            </div>

            {/* Dockerfile Preview */}
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6">
              <h3 className="text-sm font-semibold text-white mb-2 flex items-center gap-2">
                <Terminal className="w-4 h-4 text-sky-400" /> Production Dockerfile (/backend/Dockerfile)
              </h3>
              <pre className="p-4 bg-neutral-950 rounded-lg border border-neutral-800 text-xs font-mono text-neutral-300 overflow-x-auto">
{`# Multi-stage Dockerfile for Render Free Tier (reads PORT from env)
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY pom.xml .
RUN mvn dependency:go-offline -B || true
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar
ENV PORT=8080
EXPOSE \${PORT}
ENTRYPOINT ["sh", "-c", "exec java -XX:MaxRAMPercentage=75.0 -Dserver.port=\${PORT} -jar app.jar"]`}
              </pre>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
