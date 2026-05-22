// ── Auth ────────────────────────────────────────────────────────────────────

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  userId: string;
  email: string;
  role: string;
}

export interface MeResponse {
  userId: string;
  email: string;
  firebaseUid: string | null;
  role: string;
  authorities: string[];
}

// ── Users ────────────────────────────────────────────────────────────────────

export interface UserCreateRequest {
  email: string;
  rut: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  secondLastName: string;
  birthDate: string; // ISO date string YYYY-MM-DD
  roleId: number;
}

export interface UserResponse {
  id: string;
  email: string;
  rut: string;
  firstName: string;
  middleName: string | null;
  lastName: string;
  secondLastName: string;
  birthDate: string;
  roleId: number;
  roleName: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

// ── Banks ────────────────────────────────────────────────────────────────────

export interface BankResponse {
  id: number;
  name: string;
  code: string;
  active: boolean;
}

// ── Card Products ─────────────────────────────────────────────────────────────

export type CardType = 'CREDIT' | 'DEBIT' | 'PREPAID';

export interface CardProductResponse {
  id: number;
  bankId: number;
  bankName: string;
  name: string;
  cardType: CardType;
  active: boolean;
}

// ── Discounts ────────────────────────────────────────────────────────────────

export type DiscountType = 'PERCENTAGE' | 'FIXED_AMOUNT' | 'FIXED_PER_LITER';

export interface DiscountResponse {
  id: number;
  brandId: number;
  brandName: string;
  cardProductId: number | null;
  cardProductName: string | null;
  bankName: string | null;
  fuelTypeId: number | null;
  fuelTypeName: string | null;
  dayOfWeek: number | null; // 1=Mon, 7=Sun, null=every day
  discountType: DiscountType;
  discountValue: number;
  maxCap: number | null;
  description: string | null;
  startDate: string;
  endDate: string | null;
  active: boolean;
  createdAt: string;
}

export interface DiscountCreateRequest {
  brandId: number;
  cardProductId?: number;
  fuelTypeId?: number;
  dayOfWeek?: number;
  discountType: DiscountType;
  discountValue: number;
  maxCap?: number;
  description?: string;
  startDate: string;
  endDate?: string;
}

export interface DiscountUpdateRequest {
  cardProductId?: number;
  fuelTypeId?: number;
  dayOfWeek?: number;
  discountType?: DiscountType;
  discountValue?: number;
  maxCap?: number;
  description?: string;
  startDate?: string;
  endDate?: string;
}

// ── Stations ──────────────────────────────────────────────────────────────────

export interface StationSummaryResponse {
  id: string;
  name: string;
  brandId: number;
  brand: string;
  address: string;
  latitude: number;
  longitude: number;
  inMaintenance: boolean;
  distanciaKm: number | null;
  prices: CurrentPriceResponse[];
}

export interface CurrentPriceResponse {
  fuelType: string;
  price: number;
  updatedAt: string;
}

// ── Navigation ────────────────────────────────────────────────────────────────

export type ActiveSection = 'analytics' | 'users' | 'discounts';

// ── Chile Data ───────────────────────────────────────────────────────────────

export interface ChileRegion {
  id: number;
  name: string;
  communes: string[];
}
