/**
 * Device Recognition Type Definitions
 */

export type DeviceType = 'MOBILE' | 'TABLET' | 'DESKTOP' | 'UNKNOWN';

export const DeviceType = {
  MOBILE: 'MOBILE' as const,
  TABLET: 'TABLET' as const,
  DESKTOP: 'DESKTOP' as const,
  UNKNOWN: 'UNKNOWN' as const,
};

export interface DeviceInfo {
  id: string;
  fingerprint: string;
  deviceType: DeviceType;
  browser?: string;
  os?: string;
  ipAddress: string;
  trusted: boolean;
  firstSeen: string;
  lastSeen: string;
  loginCount: number;
}

export interface DeviceListResponse {
  devices: DeviceInfo[];
}

export interface TrustDeviceRequest {
  fingerprint: string;
  trusted: boolean;
}

