import java.util.Arrays;
import java.util.HashSet;

public class StopwordsSpanish {

	private static HashSet<String> m_Stopwords = null;

	public StopwordsSpanish() {

		String[] words = { "él", "ésta", "éstas", "éste", "éstos", "última", "últimas", "último", "últimos", "a",
				"añadió", "aún", "actualmente", "adelante", "además", "afirmó", "agregó", "ahí", "ahora", "al", "algún",
				"algo", "alguna", "algunas", "alguno", "algunos", "alrededor", "ambos", "ante", "anterior", "antes",
				"apenas", "aproximadamente", "aquí", "así", "aseguró", "aunque", "ayer", "bajo", "bien", "buen",
				"buena", "buenas", "bueno", "buenos", "cómo", "cada", "casi", "cerca", "cierto", "cinco", "comentó",
				"como", "con", "conocer", "consideró", "considera", "contra", "cosas", "creo", "cual", "cuales",
				"cualquier", "cuando", "cuanto", "cuatro", "cuenta", "da", "dado", "dan", "dar", "de", "debe", "deben",
				"debido", "decir", "dejó", "del", "demás", "dentro", "desde", "después", "dice", "dicen", "dicho",
				"dieron", "diferente", "diferentes", "dijeron", "dijo", "dio", "donde", "dos", "durante", "e",
				"ejemplo", "el", "ella", "ellas", "ello", "ellos", "embargo", "en", "encuentra", "entonces", "entre",
				"era", "eran", "es", "esa", "esas", "ese", "eso", "esos", "está", "están", "esta", "estaba", "estaban",
				"estamos", "estar", "estará", "estas", "este", "esto", "estos", "estoy", "estuvo", "ex", "existe",
				"existen", "explicó", "expresó", "fin", "fue", "fuera", "fueron", "gran", "grandes", "ha", "había",
				"habían", "haber", "habrá", "hace", "hacen", "hacer", "hacerlo", "hacia", "haciendo", "han", "hasta",
				"hay", "haya", "he", "hecho", "hemos", "hicieron", "hizo", "hoy", "hubo", "igual", "incluso", "indicó",
				"informó", "junto", "la", "lado", "las", "le", "les", "llegó", "lleva", "llevar", "lo", "los", "luego",
				"lugar", "más", "manera", "manifestó", "mayor", "me", "mediante", "mejor", "mencionó", "menos", "mi",
				"mientras", "misma", "mismas", "mismo", "mismos", "momento", "mucha", "muchas", "mucho", "muchos",
				"muy", "nada", "nadie", "ni", "ningún", "ninguna", "ningunas", "ninguno", "ningunos", "no", "nos",
				"nosotras", "nosotros", "nuestra", "nuestras", "nuestro", "nuestros", "nueva", "nuevas", "nuevo",
				"nuevos", "nunca", "o", "ocho", "otra", "otras", "otro", "otros", "para", "parece", "parte", "partir",
				"pasada", "pasado", "pero", "pesar", "poca", "pocas", "poco", "pocos", "podemos", "podrá", "podrán",
				"podría", "podrían", "poner", "por", "porque", "posible", "próximo", "próximos", "primer", "primera",
				"primero", "primeros", "principalmente", "propia", "propias", "propio", "propios", "pudo", "pueda",
				"puede", "pueden", "pues", "qué", "que", "quedó", "queremos", "quién", "quien", "quienes", "quiere",
				"realizó", "realizado", "realizar", "respecto", "sí", "sólo", "se", "señaló", "sea", "sean", "según",
				"segunda", "segundo", "seis", "ser", "será", "serán", "sería", "si", "sido", "siempre", "siendo",
				"siete", "sigue", "siguiente", "sin", "sino", "sobre", "sola", "solamente", "solas", "solo", "solos",
				"son", "su", "sus", "tal", "también", "tampoco", "tan", "tanto", "tenía", "tendrá", "tendrán",
				"tenemos", "tener", "tenga", "tengo", "tenido", "tercera", "tiene", "tienen", "toda", "todas",
				"todavía", "todo", "todos", "total", "tras", "trata", "través", "tres", "tuvo", "un", "una", "unas",
				"uno", "unos", "usted", "va", "vamos", "van", "varias", "varios", "veces", "ver", "vez", "y", "ya",
				"yo", "q", "u", "d", "candidatos", "via", "vía", "rafael", "pardo", "vicente", "roux", "pardeando",
				"alo", "mas", "lópez", "peñalosa", "enrique", "años", "quieren", "únicas", "ud", "bogotá", "alcalde",
				"alcaldía", "alcaldia", "candidato", "carlos", "ciudad", "ht", "bogota", "btá", "bta", "estan",
				"mañana", "toma", "necesita", "día", "dia", "fm", "campaña", "clara", "lopez", "htt", "h", "santos",
				"pacho", "francisco", "-", "http", "rt", "daniel", "raisbeck", "am", "pm", "debate"};

		if (m_Stopwords == null) {
			m_Stopwords = new HashSet<String>();
			m_Stopwords.addAll(Arrays.asList(words));
		}
	}

	public boolean isStopword(String str) {
		return m_Stopwords.contains(str.toLowerCase());
	}
}