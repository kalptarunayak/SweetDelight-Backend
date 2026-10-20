/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect } from 'react';
import {
  Database,
  RefreshCw,
  CheckCircle2,
  AlertCircle,
  Table,
  Layers,
  Code,
  Sparkles,
  ExternalLink,
  ShieldCheck,
  Search,
  Filter,
  Check,
  Wifi,
  WifiOff
} from 'lucide-react';
import {
  getSupabaseClient,
  saveSupabaseConfig,
  clearSupabaseConfig,
  getSupabaseConfigStatus
} from '../lib/supabase';

// Seeded fallback data (matches Flyway migrations V1 & V2)
const SEEDED_DATABASE: Record<string, any[]> = {
  products: [
    {
      id: 1,
      category_id: 1,
      name: 'Belgian Chocolate Truffle Cake',
      description: 'Rich 54% dark chocolate ganache layered with moist chocolate sponge and cocoa glaze.',
      image_url: 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800',
      is_in_stock: true,
      variants: [
        { id: 1, title: '500g', price_inr: 699.00, is_in_stock: true },
        { id: 2, title: '1kg', price_inr: 1299.00, is_in_stock: true }
      ]
    },
    {
      id: 2,
      category_id: 1,
      name: 'Classic Red Velvet Cake',
      description: 'Velvety crimson layers filled with silky Madagascar cream cheese frosting.',
      image_url: 'https://images.unsplash.com/photo-1616541823729-00fe0aacd32c?w=800',
      is_in_stock: true,
      variants: [
        { id: 3, title: '500g', price_inr: 749.00, is_in_stock: true },
        { id: 4, title: '1kg', price_inr: 1399.00, is_in_stock: true }
      ]
    },
    {
      id: 3,
      category_id: 1,
      name: 'Fresh Alphonso Mango Cake',
      description: 'Summer delicacy layered with fresh Ratnagiri mango compote and light vanilla cream.',
      image_url: 'https://images.unsplash.com/photo-1535141192574-5d4897c13136?w=800',
      is_in_stock: true,
      variants: [
        { id: 5, title: '500g', price_inr: 799.00, is_in_stock: true },
        { id: 6, title: '1kg', price_inr: 1499.00, is_in_stock: true }
      ]
    },
    {
      id: 4,
      category_id: 1,
      name: 'Black Forest Gateau',
      description: 'Classic German black forest cake with sour cherries, kirsch essence, and chocolate shavings.',
      image_url: 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=800',
      is_in_stock: true,
      variants: [
        { id: 7, title: '500g', price_inr: 649.00, is_in_stock: true },
        { id: 8, title: '1kg', price_inr: 1199.00, is_in_stock: true }
      ]
    },
    {
      id: 5,
      category_id: 1,
      name: 'Butterscotch Crunch Cake',
      description: 'Soft caramel sponge dressed in praline butterscotch crunch and golden butter glaze.',
      image_url: 'https://images.unsplash.com/photo-1588195538326-c5b1e9f80a1b?w=800',
      is_in_stock: true,
      variants: [
        { id: 9, title: '500g', price_inr: 599.00, is_in_stock: true },
        { id: 10, title: '1kg', price_inr: 1099.00, is_in_stock: true }
      ]
    },
    {
      id: 6,
      category_id: 1,
      name: 'Royal Rasmalai Fusion Cake',
      description: 'Cardamom-infused sponge soaked in rich saffron milk, topped with tender rasmalai and pistachios.',
      image_url: 'https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=800',
      is_in_stock: true,
      variants: [
        { id: 11, title: '500g', price_inr: 849.00, is_in_stock: true },
        { id: 12, title: '1kg', price_inr: 1599.00, is_in_stock: true }
      ]
    },
    {
      id: 7,
      category_id: 2,
      name: 'Artisanal Dark Chocolate Hazelnut Rocks',
      description: 'Crunchy roasted Piedmont hazelnuts enrobed in velvety 70% dark Belgian chocolate.',
      image_url: 'https://images.unsplash.com/photo-1549007994-cb92caebd54b?w=800',
      is_in_stock: true,
      variants: [
        { id: 13, title: 'Box of 6', price_inr: 450.00, is_in_stock: true },
        { id: 14, title: 'Box of 12', price_inr: 850.00, is_in_stock: true }
      ]
    },
    {
      id: 8,
      category_id: 2,
      name: 'Roasted Almond Dark Chocolate Bar',
      description: 'Single-origin dark chocolate blended with whole roasted Californian almonds.',
      image_url: 'https://images.unsplash.com/photo-1606312619070-d48b4c652a52?w=800',
      is_in_stock: true,
      variants: [
        { id: 15, title: '100g Bar', price_inr: 299.00, is_in_stock: true }
      ]
    }
  ],
  categories: [
    { id: 1, name: 'Cakes', slug: 'cakes', description: 'Freshly baked artisanal cakes, celebration cakes, and eggless gourmet creations.' },
    { id: 2, name: 'Chocolates', slug: 'chocolates', description: 'Handcrafted truffles, luxury bonbons, single-origin bars, and gift boxes.' }
  ],
  banners: [
    {
      id: 1,
      title: 'Grand Celebration Treat 🍰',
      subtitle: 'Enjoy flat 15% discount on all cakes and handcrafted chocolates this season!',
      image_url: 'https://images.unsplash.com/photo-1535141192574-5d4897c13136?w=1200',
      discount_percent: 15,
      coupon_code: 'SWEET15',
      accent_color: '#E11D48',
      start_at: '2026-10-19T00:00:00Z',
      end_at: '2027-01-19T00:00:00Z',
      is_published: true
    }
  ],
  orders: [
    {
      id: 1,
      order_number: 'ORD-20261020-584912',
      customer_name: 'Aarav Sharma',
      whatsapp_number: '+919876543210',
      delivery_address: 'Flat 402, Sea Green Apts, Hill Road, Bandra West, Mumbai',
      delivery_lat: 19.0620,
      delivery_lng: 72.8350,
      delivery_distance_km: 0.64,
      delivery_slot: 'Today 5:00 PM - 7:00 PM',
      payment_method: 'ONLINE',
      subtotal_inr: 699.00,
      discount_inr: 104.85,
      coupon_code: 'SWEET15',
      total_inr: 594.15,
      status: 'OUT_FOR_DELIVERY',
      rider_name: 'Vikram Singh',
      rider_phone: '+919876543299',
      rider_lat: 19.0608,
      rider_lng: 72.8318
    }
  ],
  app_settings: [
    {
      id: 1,
      shop_lat: 19.0596,
      shop_lng: 72.8295,
      delivery_radius_km: 5.0,
      updated_at: '2026-10-20T04:15:00Z'
    }
  ],
  party_rooms: [
    {
      id: 1,
      name: 'The Sweet Celebration Lounge',
      description: 'A beautifully decorated private lounge designed for cake cutting celebrations, birthday memories, and anniversary parties.',
      capacity: 16,
      is_active: true
    }
  ],
  room_bookings: [
    {
      id: 1,
      room_id: 1,
      booking_date: '2026-11-20',
      start_time: '14:00:00',
      end_time: '16:00:00',
      guest_count: 10,
      contact_name: 'Priya Kapoor',
      contact_phone: '+919876543212',
      status: 'APPROVED',
      notes: 'Anniversary cake cutting'
    }
  ]
};

export default function SupabaseDataViewer() {
  const [selectedTable, setSelectedTable] = useState<string>('products');
  const [data, setData] = useState<any[]>(SEEDED_DATABASE['products']);
  const [loading, setLoading] = useState(false);
  const [isLiveConnected, setIsLiveConnected] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [viewMode, setViewMode] = useState<'cards' | 'table' | 'json'>('cards');
  const [searchQuery, setSearchQuery] = useState('');

  // Configuration modal state
  const [showConfig, setShowConfig] = useState(false);
  const [supabaseUrl, setSupabaseUrl] = useState('');
  const [supabaseAnonKey, setSupabaseAnonKey] = useState('');

  useEffect(() => {
    const status = getSupabaseConfigStatus();
    if (status.hasConfig) {
      setSupabaseUrl(status.url);
      fetchFromSupabase(selectedTable);
    } else {
      setData(SEEDED_DATABASE[selectedTable] || []);
    }
  }, [selectedTable]);

  const fetchFromSupabase = async (tableName: string) => {
    const client = getSupabaseClient();
    if (!client) {
      setIsLiveConnected(false);
      setData(SEEDED_DATABASE[tableName] || []);
      return;
    }

    setLoading(true);
    setErrorMsg(null);
    try {
      let query = client.from(tableName).select('*');
      if (tableName === 'products') {
        // Also attempt to load variants if relational schema is present
        query = client.from('products').select('*, product_variants(*)');
      }

      const { data: result, error } = await query;
      if (error) {
        throw error;
      }

      if (result && result.length > 0) {
        setData(result);
        setIsLiveConnected(true);
      } else {
        // Fallback to seeded demo if table is empty
        setData(SEEDED_DATABASE[tableName] || []);
        setIsLiveConnected(true);
      }
    } catch (err: any) {
      console.warn('Supabase query notice:', err.message);
      setErrorMsg(err.message || 'Could not fetch from live Supabase instance');
      setIsLiveConnected(false);
      setData(SEEDED_DATABASE[tableName] || []);
    } finally {
      setLoading(false);
    }
  };

  const handleSaveConnection = () => {
    if (!supabaseUrl || !supabaseAnonKey) {
      setErrorMsg('Please provide both Supabase URL and Anon Key');
      return;
    }
    saveSupabaseConfig(supabaseUrl, supabaseAnonKey);
    setShowConfig(false);
    fetchFromSupabase(selectedTable);
  };

  const handleDisconnect = () => {
    clearSupabaseConfig();
    setIsLiveConnected(false);
    setSupabaseUrl('');
    setSupabaseAnonKey('');
    setData(SEEDED_DATABASE[selectedTable] || []);
  };

  const filteredData = data.filter((item) => {
    if (!searchQuery) return true;
    const str = JSON.stringify(item).toLowerCase();
    return str.includes(searchQuery.toLowerCase());
  });

  const tables = [
    { id: 'products', name: 'Products & Variants', count: 6 },
    { id: 'categories', name: 'Categories', count: 2 },
    { id: 'banners', name: 'Banners & Coupons', count: 1 },
    { id: 'orders', name: 'Orders & Tracking', count: 1 },
    { id: 'app_settings', name: 'App Settings & Radius', count: 1 },
    { id: 'party_rooms', name: 'Party Rooms', count: 1 },
    { id: 'room_bookings', name: 'Room Bookings', count: 1 }
  ];

  return (
    <div className="space-y-6">
      {/* Top Status & Connection Bar */}
      <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-5 shadow-lg flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-emerald-500/10 border border-emerald-500/20 flex items-center justify-center">
            <Database className="w-5 h-5 text-emerald-400" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h2 className="text-base font-bold text-white">Supabase PostgreSQL Data Engine</h2>
              {isLiveConnected ? (
                <span className="px-2 py-0.5 rounded-full text-[11px] font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 flex items-center gap-1">
                  <Wifi className="w-3 h-3 text-emerald-400" /> Live Supabase Active
                </span>
              ) : (
                <span className="px-2 py-0.5 rounded-full text-[11px] font-semibold bg-amber-500/10 text-amber-400 border border-amber-500/20 flex items-center gap-1">
                  <WifiOff className="w-3 h-3 text-amber-400" /> Flyway Seeded Schema Mode
                </span>
              )}
            </div>
            <p className="text-xs text-neutral-400">
              {isLiveConnected
                ? `Querying directly from live Supabase project table: ${selectedTable}`
                : 'Rendering database records seeded by Flyway migration (V1__initial_schema.sql & V2__seed_data.sql)'}
            </p>
          </div>
        </div>

        <div className="flex items-center gap-2 flex-wrap">
          <button
            onClick={() => fetchFromSupabase(selectedTable)}
            disabled={loading}
            className="px-3 py-1.5 bg-neutral-800 hover:bg-neutral-700 text-neutral-200 text-xs font-semibold rounded-lg border border-neutral-700 flex items-center gap-1.5 transition disabled:opacity-50"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
            Refresh Table
          </button>

          <button
            onClick={() => setShowConfig(!showConfig)}
            className="px-3 py-1.5 bg-rose-600 hover:bg-rose-500 text-white text-xs font-semibold rounded-lg shadow-md shadow-rose-600/30 flex items-center gap-1.5 transition"
          >
            <ExternalLink className="w-3.5 h-3.5" />
            {isLiveConnected ? 'Edit Supabase Config' : 'Connect Live Supabase'}
          </button>
        </div>
      </div>

      {/* Supabase Connection Setup Box (Collapsible) */}
      {showConfig && (
        <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-5 space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-semibold text-white flex items-center gap-2">
              <ShieldCheck className="w-4 h-4 text-emerald-400" /> Connect Your Supabase Project Credentials
            </h3>
            <span className="text-xs text-neutral-400">From Supabase Project Settings &gt; API</span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-neutral-300 mb-1">
                Project URL (<code className="text-emerald-400">VITE_SUPABASE_URL</code>)
              </label>
              <input
                type="text"
                placeholder="https://xyzcompany.supabase.co"
                value={supabaseUrl}
                onChange={(e) => setSupabaseUrl(e.target.value)}
                className="w-full bg-neutral-950 border border-neutral-800 rounded-lg px-3 py-2 text-xs font-mono text-neutral-100 focus:outline-none focus:border-emerald-500"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-neutral-300 mb-1">
                Anon Public Key (<code className="text-emerald-400">VITE_SUPABASE_ANON_KEY</code>)
              </label>
              <input
                type="password"
                placeholder="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                value={supabaseAnonKey}
                onChange={(e) => setSupabaseAnonKey(e.target.value)}
                className="w-full bg-neutral-950 border border-neutral-800 rounded-lg px-3 py-2 text-xs font-mono text-neutral-100 focus:outline-none focus:border-emerald-500"
              />
            </div>
          </div>

          {errorMsg && (
            <div className="p-3 bg-rose-950/40 border border-rose-500/40 rounded-lg text-xs text-rose-300 flex items-center gap-2">
              <AlertCircle className="w-4 h-4 text-rose-400 shrink-0" />
              <span>{errorMsg}</span>
            </div>
          )}

          <div className="flex justify-end gap-2 pt-2 border-t border-neutral-800">
            {isLiveConnected && (
              <button
                onClick={handleDisconnect}
                className="px-3 py-1.5 bg-neutral-800 hover:bg-neutral-700 text-xs font-semibold text-rose-400 rounded-lg transition"
              >
                Disconnect
              </button>
            )}
            <button
              onClick={() => setShowConfig(false)}
              className="px-3 py-1.5 bg-neutral-800 hover:bg-neutral-700 text-xs font-semibold text-neutral-300 rounded-lg transition"
            >
              Cancel
            </button>
            <button
              onClick={handleSaveConnection}
              className="px-4 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-semibold rounded-lg transition shadow-md shadow-emerald-600/30"
            >
              Save &amp; Connect Live
            </button>
          </div>
        </div>
      )}

      {/* Table Selector Pills & Filter Controls */}
      <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-3">
        {/* Table Selector */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1">
          {tables.map((t) => {
            const active = selectedTable === t.id;
            return (
              <button
                key={t.id}
                onClick={() => setSelectedTable(t.id)}
                className={`px-3 py-1.5 rounded-lg text-xs font-medium whitespace-nowrap transition flex items-center gap-1.5 ${
                  active
                    ? 'bg-rose-600 text-white shadow-md shadow-rose-600/20'
                    : 'bg-neutral-900 hover:bg-neutral-800 text-neutral-400 hover:text-neutral-200 border border-neutral-800'
                }`}
              >
                <Table className="w-3.5 h-3.5" />
                {t.name}
              </button>
            );
          })}
        </div>

        {/* View Mode & Search */}
        <div className="flex items-center gap-2">
          <div className="relative">
            <Search className="w-3.5 h-3.5 text-neutral-500 absolute left-2.5 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Search table..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="bg-neutral-900 border border-neutral-800 rounded-lg pl-8 pr-3 py-1 text-xs text-neutral-200 placeholder:text-neutral-500 focus:outline-none focus:border-rose-500 w-36 sm:w-48"
            />
          </div>

          <div className="flex rounded-lg border border-neutral-800 bg-neutral-900 p-0.5 text-xs">
            <button
              onClick={() => setViewMode('cards')}
              className={`px-2.5 py-1 rounded font-medium transition ${
                viewMode === 'cards' ? 'bg-neutral-800 text-white' : 'text-neutral-400 hover:text-neutral-200'
              }`}
            >
              Cards
            </button>
            <button
              onClick={() => setViewMode('table')}
              className={`px-2.5 py-1 rounded font-medium transition ${
                viewMode === 'table' ? 'bg-neutral-800 text-white' : 'text-neutral-400 hover:text-neutral-200'
              }`}
            >
              Grid
            </button>
            <button
              onClick={() => setViewMode('json')}
              className={`px-2.5 py-1 rounded font-medium transition ${
                viewMode === 'json' ? 'bg-neutral-800 text-white' : 'text-neutral-400 hover:text-neutral-200'
              }`}
            >
              JSON
            </button>
          </div>
        </div>
      </div>

      {/* Render Table Data */}
      {viewMode === 'cards' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filteredData.map((row, idx) => (
            <div
              key={idx}
              className="bg-neutral-900 border border-neutral-800 rounded-xl overflow-hidden hover:border-neutral-700 transition flex flex-col justify-between"
            >
              {row.image_url && (
                <div className="h-44 w-full overflow-hidden bg-neutral-950 relative">
                  <img
                    src={row.image_url}
                    alt={row.name || row.title}
                    className="w-full h-full object-cover group-hover:scale-105 transition duration-500"
                  />
                  <div className="absolute top-2 right-2 flex gap-1">
                    {row.coupon_code && (
                      <span className="px-2 py-0.5 rounded text-[10px] font-mono font-bold bg-rose-600 text-white shadow">
                        {row.coupon_code}
                      </span>
                    )}
                    {row.discount_percent && (
                      <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-amber-500 text-neutral-950 shadow">
                        {row.discount_percent}% OFF
                      </span>
                    )}
                    {row.is_in_stock !== undefined && (
                      <span
                        className={`px-2 py-0.5 rounded text-[10px] font-semibold ${
                          row.is_in_stock ? 'bg-emerald-500/90 text-white' : 'bg-rose-500/90 text-white'
                        }`}
                      >
                        {row.is_in_stock ? 'In Stock' : 'Out of Stock'}
                      </span>
                    )}
                  </div>
                </div>
              )}

              <div className="p-4 space-y-2.5 flex-1">
                <div className="flex items-start justify-between gap-2">
                  <h4 className="font-bold text-white text-sm">
                    {row.name || row.title || row.order_number || `Record #${row.id}`}
                  </h4>
                  {row.status && (
                    <span className="px-2 py-0.5 rounded text-[10px] font-mono font-bold bg-neutral-800 text-rose-400 border border-neutral-700">
                      {row.status}
                    </span>
                  )}
                </div>

                {row.description && (
                  <p className="text-xs text-neutral-400 line-clamp-2 leading-relaxed">{row.description}</p>
                )}

                {row.subtitle && (
                  <p className="text-xs text-neutral-400 line-clamp-2">{row.subtitle}</p>
                )}

                {/* Variants preview if product */}
                {row.variants && row.variants.length > 0 && (
                  <div className="pt-2 border-t border-neutral-800/80">
                    <div className="text-[10px] text-neutral-500 font-medium mb-1">Available Variants:</div>
                    <div className="flex flex-wrap gap-1.5">
                      {row.variants.map((v: any, vIdx: number) => (
                        <span
                          key={vIdx}
                          className="px-2 py-0.5 rounded bg-neutral-950 text-neutral-300 text-[11px] font-mono border border-neutral-800"
                        >
                          {v.title}: <strong className="text-rose-400">₹{v.price_inr}</strong>
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                {/* Order Details */}
                {row.total_inr && (
                  <div className="pt-2 border-t border-neutral-800/80 text-xs flex justify-between items-center font-mono">
                    <span className="text-neutral-400">{row.delivery_slot || row.payment_method}</span>
                    <span className="text-white font-bold text-sm">₹{row.total_inr}</span>
                  </div>
                )}

                {/* App Settings details */}
                {row.delivery_radius_km && (
                  <div className="space-y-1 text-xs font-mono text-neutral-300 pt-1">
                    <div>Shop Coordinates: {row.shop_lat}, {row.shop_lng}</div>
                    <div className="text-amber-400 font-bold">Max Delivery Radius: {row.delivery_radius_km} km</div>
                  </div>
                )}

                {/* Booking details */}
                {row.booking_date && (
                  <div className="space-y-1 text-xs text-neutral-300 pt-1">
                    <div>Date: <span className="font-mono">{row.booking_date}</span> ({row.start_time} - {row.end_time})</div>
                    <div>Guests: {row.guest_count} | Contact: {row.contact_name}</div>
                  </div>
                )}
              </div>

              <div className="px-4 py-2 bg-neutral-950/60 border-t border-neutral-800 flex items-center justify-between text-[10px] text-neutral-500 font-mono">
                <span>Table: {selectedTable}</span>
                <span>ID: {row.id}</span>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Grid / Table View */}
      {viewMode === 'table' && (
        <div className="bg-neutral-900 border border-neutral-800 rounded-xl overflow-x-auto shadow-lg">
          <table className="w-full text-left text-xs text-neutral-300">
            <thead className="bg-neutral-950 border-b border-neutral-800 text-[11px] font-mono uppercase text-neutral-400">
              <tr>
                {filteredData.length > 0 &&
                  Object.keys(filteredData[0]).map((key) => (
                    <th key={key} className="px-4 py-3 font-semibold whitespace-nowrap">
                      {key}
                    </th>
                  ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-neutral-800">
              {filteredData.map((row, idx) => (
                <tr key={idx} className="hover:bg-neutral-800/40 transition">
                  {Object.keys(row).map((k) => (
                    <td key={k} className="px-4 py-2.5 max-w-xs truncate font-mono text-[11px]">
                      {typeof row[k] === 'object' ? JSON.stringify(row[k]) : String(row[k])}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Raw JSON View */}
      {viewMode === 'json' && (
        <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-4 shadow-lg">
          <pre className="p-4 bg-neutral-950 rounded-lg text-xs font-mono text-neutral-300 overflow-x-auto max-h-[500px]">
            {JSON.stringify(filteredData, null, 2)}
          </pre>
        </div>
      )}
    </div>
  );
}
