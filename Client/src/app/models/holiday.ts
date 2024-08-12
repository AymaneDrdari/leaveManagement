export interface Holiday {
  id?: string; // Assurez-vous que le type UUID est correct
  description: string;
  date_debut: string; // Utilisez string au lieu de Date pour les API JSON
  date_fin: string; // Utilisez string au lieu de Date pour les API JSON
  is_fixe: boolean;
}
