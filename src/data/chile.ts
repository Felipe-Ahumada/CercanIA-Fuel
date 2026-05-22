import type { ChileRegion } from '../types';

export const CHILE_REGIONS: ChileRegion[] = [
  { id: 1,  name: 'Arica y Parinacota', communes: ['Arica', 'Putre', 'Camarones', 'General Lagos'] },
  { id: 2,  name: 'Tarapacá', communes: ['Iquique', 'Alto Hospicio', 'Pozo Almonte', 'Pica', 'Colchane', 'Camiña', 'Huara'] },
  { id: 3,  name: 'Antofagasta', communes: ['Antofagasta', 'Calama', 'Tocopilla', 'Mejillones', 'Sierra Gorda', 'Taltal', 'María Elena', 'Ollague', 'San Pedro de Atacama'] },
  { id: 4,  name: 'Atacama', communes: ['Copiapó', 'Vallenar', 'Chañaral', 'Caldera', 'Tierra Amarilla', 'Diego de Almagro', 'Freirina', 'Huasco', 'Alto del Carmen'] },
  { id: 5,  name: 'Coquimbo', communes: ['La Serena', 'Coquimbo', 'Ovalle', 'Illapel', 'Andacollo', 'Vicuña', 'Paihuano', 'Monte Patria', 'Combarbalá', 'Punitaqui', 'Río Hurtado', 'Los Vilos', 'Salamanca', 'Canela'] },
  { id: 6,  name: 'Valparaíso', communes: ['Valparaíso', 'Viña del Mar', 'Quilpué', 'Concón', 'Villa Alemana', 'San Antonio', 'Quillota', 'Los Andes', 'San Felipe', 'Casablanca', 'Limache', 'Olmué', 'Petorca', 'La Ligua'] },
  { id: 7,  name: 'Metropolitana de Santiago', communes: ['Santiago', 'Providencia', 'Las Condes', 'Maipú', 'Puente Alto', 'La Florida', 'Peñalolén', 'Ñuñoa', 'San Bernardo', 'El Bosque', 'Pudahuel', 'Recoleta', 'La Pintana', 'Estación Central', 'Cerrillos', 'Buin', 'Colina', 'Talagante'] },
  { id: 8,  name: "O'Higgins", communes: ['Rancagua', 'San Fernando', 'Pichilemu', 'Santa Cruz', 'Machalí', 'Graneros', 'Requínoa', 'Rengo', 'Peumo', 'Las Cabras', 'Pichidegua', 'Litueche'] },
  { id: 9,  name: 'Maule', communes: ['Talca', 'Curicó', 'Linares', 'Cauquenes', 'Constitución', 'San Javier', 'Parral', 'Molina', 'Chanco', 'Pelluhue', 'Sagrada Familia', 'Retiro', 'Longaví', 'Yerbas Buenas'] },
  { id: 10, name: 'Ñuble', communes: ['Chillán', 'Chillán Viejo', 'San Carlos', 'Bulnes', 'Coihueco', 'Quirihue', 'Yungay', 'El Carmen', 'Pemuco', 'Pinto', 'Cobquecura', 'Ñiquén', 'San Nicolás', 'San Ignacio', 'Ninhue', 'Portezuelo', 'Trehuaco', 'Coelemu', 'Quillón', 'Ranquil', 'Treguaco'] },
  { id: 11, name: 'Biobío', communes: ['Concepción', 'Los Ángeles', 'Talcahuano', 'Chiguayante', 'Coronel', 'Lota', 'San Pedro de la Paz', 'Hualpén', 'Tomé', 'Penco', 'Florida', 'Mulchén', 'Nacimiento', 'Santa Bárbara', 'Quilaco', 'Lebu'] },
  { id: 12, name: 'La Araucanía', communes: ['Temuco', 'Villarrica', 'Pucón', 'Padre Las Casas', 'Angol', 'Victoria', 'Lautaro', 'Freire', 'Pitrufquén', 'Gorbea', 'Traiguén', 'Curacautín', 'Lonquimay', 'Melipeuco'] },
  { id: 13, name: 'Los Ríos', communes: ['Valdivia', 'La Unión', 'Río Bueno', 'Panguipulli', 'Futrono', 'Lago Ranco', 'Los Lagos', 'Máfil', 'Lanco', 'Mariquina', 'Paillaco', 'Corral'] },
  { id: 14, name: 'Los Lagos', communes: ['Puerto Montt', 'Osorno', 'Castro', 'Puerto Varas', 'Ancud', 'Calbuco', 'Quellón', 'Maullín', 'Frutillar', 'Llanquihue', 'Purranque', 'Río Negro', 'Chaitén', 'Palena'] },
  { id: 15, name: 'Aysén', communes: ['Coyhaique', 'Puerto Aysén', 'Chile Chico', 'Cochrane', 'Lago Verde', 'Guaitecas', 'O\'Higgins', 'Río Ibáñez', 'Tortel'] },
  { id: 16, name: 'Magallanes y la Antártica Chilena', communes: ['Punta Arenas', 'Puerto Natales', 'Porvenir', 'Puerto Williams', 'Timaukel', 'Laguna Blanca', 'Río Verde', 'San Gregorio', 'Cabo de Hornos', 'Antártica'] },
];

export const BENCINERAS = ['Copec', 'Shell', 'Petrobras', 'Aramco', 'Enex', 'Terpel'];

export const BANCOS = [
  'Banco de Chile',
  'Santander',
  'BCI',
  'Scotiabank',
  'Itaú',
  'BICE',
  'Falabella',
];

export const DIAS_SEMANA = [
  { value: 1, label: 'Lun' },
  { value: 2, label: 'Mar' },
  { value: 3, label: 'Mié' },
  { value: 4, label: 'Jue' },
  { value: 5, label: 'Vie' },
  { value: 6, label: 'Sáb' },
  { value: 7, label: 'Dom' },
];

export const DAY_NAMES: Record<number, string> = {
  1: 'Lunes',
  2: 'Martes',
  3: 'Miércoles',
  4: 'Jueves',
  5: 'Viernes',
  6: 'Sábado',
  7: 'Domingo',
};
