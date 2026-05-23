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
  vehicleCount:       number;
  totalTransactions:  number;
  totalSavings:       number;
}

export interface VehicleResponse {
  id: string;
  vehicleModelId: number;
  brandName: string;
  modelName: string;
  fuelTypeId: number;
  fuelTypeName: string;
  licensePlate: string;
  year: number;
}

export interface TransactionResponse {
  id: string;
  userId: string;
  vehicleId: string | null;
  stationId: string;
  stationName: string;
  stationBrand: string | null;
  fuelTypeId: number;
  fuelTypeName: string;
  cardProductId: number | null;
  cardProductName: string | null;
  discountId: number | null;
  unitPrice: number;
  liters: number;
  grossAmount: number;
  discountAmount: number;
  finalAmount: number;
  transactionDate: string;
  notes: string | null;
  createdAt: string;
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
  cardType: 'CREDIT' | 'DEBIT' | 'PREPAID' | null;
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
  endDate: string;
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
  active?: boolean;
}

// ── Navigation ────────────────────────────────────────────────────────────────

export type ActiveSection = 'analytics' | 'users' | 'discounts';

// ── Analytics ─────────────────────────────────────────────────────────────────

export interface MonthlyCount { month: string; count: number; }
export interface NamedCount   { name: string;  count: number; }
export interface DiscountUsage {
  brand: string; bank: string; cardProduct: string;
  value: number; type: string; userCount: number;
}
export interface HourlyCount  { hour: number;  count: number; }

export interface BrandDetail {
  brand:       string;
  recargas:    number;
  uniqueUsers: number;
  ahorroTotal: number;
  litrosTotal: number;
  tendencia:   number;
}

export interface AnalyticsResponse {
  usersByMonth:        MonthlyCount[];
  stationsByBrand:     NamedCount[];
  fuelDistribution:    NamedCount[];
  topDiscountsByUsage: DiscountUsage[];
  transactionsByHour:  HourlyCount[];
  brandDetails:        BrandDetail[];
  totalUsers:          number;
  activeUsers:         number;
  totalStations:       number;
  activeDiscounts:     number;
  totalDiscountUses:   number;
}

