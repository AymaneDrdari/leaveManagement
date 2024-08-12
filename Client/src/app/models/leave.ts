// models/leave.ts
export interface Leave {
  id_conge?: string;
  date_debut: string;
  date_fin: string;
  collaborateur_email: string;
  description: string;
  nombre_jours_pris: number;
  demi_journee_matin: boolean;
  demi_journee_soir: boolean;
}
