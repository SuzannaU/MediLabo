package medilabo.risksapp.model;

import java.util.List;

public class Triggers {

    private static final List<String> triggers = List.of(
            "Hémoglobine A1C",
            "Microalbumine",
            "Taille",
            "Poids",
            "Fumeur",
            "Fumeuse",
            "Anormal",
            "Cholestérol",
            "Vertige",
            "Rechute",
            "Réaction",
            "Anticorps"
    );

    public static List<String> getTriggers() {
        return triggers;
    }
}
