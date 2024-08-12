import {EquipeDTO} from "./equipe.model";
import {Niveau} from "./niveau.model";

export interface Collaborateur {
  id?: string;
  nom: string;
  prenom: string;
  email: string;
  niveau: Niveau;
  equipe: EquipeDTO;
  type: string;
  nombre_jours_payes_mois: number;
  date_entree_projet: Date;
  date_sortie_projet?: Date;
  role?: string;
}
