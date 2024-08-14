export interface EquipeDTO {
  code: string;
  nom: string;
  description?: string;  // Facultatif
  dateCreation?: Date;   // Facultatif
  couleur: string;
}
