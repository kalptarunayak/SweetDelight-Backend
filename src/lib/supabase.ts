/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { createClient, SupabaseClient } from '@supabase/supabase-js';

// Environment variables or localStorage override for dynamic browser testing
const envUrl = (import.meta as any).env?.VITE_SUPABASE_URL || '';
const envKey = (import.meta as any).env?.VITE_SUPABASE_ANON_KEY || '';

let cachedClient: SupabaseClient | null = null;
let currentUrl = envUrl;
let currentKey = envKey;

export function getSupabaseClient(customUrl?: string, customKey?: string): SupabaseClient | null {
  const url = customUrl || currentUrl || localStorage.getItem('sweetdelight_supabase_url') || '';
  const key = customKey || currentKey || localStorage.getItem('sweetdelight_supabase_key') || '';

  if (!url || !key) {
    return null;
  }

  if (cachedClient && currentUrl === url && currentKey === key) {
    return cachedClient;
  }

  try {
    currentUrl = url;
    currentKey = key;
    cachedClient = createClient(url, key);
    return cachedClient;
  } catch (err) {
    console.error('Failed to initialize Supabase client:', err);
    return null;
  }
}

export function saveSupabaseConfig(url: string, key: string) {
  currentUrl = url.trim();
  currentKey = key.trim();
  localStorage.setItem('sweetdelight_supabase_url', currentUrl);
  localStorage.setItem('sweetdelight_supabase_key', currentKey);
  if (currentUrl && currentKey) {
    cachedClient = createClient(currentUrl, currentKey);
  } else {
    cachedClient = null;
  }
}

export function clearSupabaseConfig() {
  currentUrl = '';
  currentKey = '';
  localStorage.removeItem('sweetdelight_supabase_url');
  localStorage.removeItem('sweetdelight_supabase_key');
  cachedClient = null;
}

export interface SupabaseConfigStatus {
  hasConfig: boolean;
  url: string;
  maskedKey: string;
}

export function getSupabaseConfigStatus(): SupabaseConfigStatus {
  const url = currentUrl || localStorage.getItem('sweetdelight_supabase_url') || envUrl || '';
  const key = currentKey || localStorage.getItem('sweetdelight_supabase_key') || envKey || '';
  const maskedKey = key.length > 8 ? key.substring(0, 4) + '...' + key.substring(key.length - 4) : '';
  return {
    hasConfig: Boolean(url && key),
    url,
    maskedKey
  };
}
