package com.urielhuerta.criterio.domain.engine

data class DarkTactic(
    val id: String,
    val name: String,
    val darkCategory: String, // "MAQUIAVELISMO", "NARCISISMO", "PSICOPATÍA_SUBCLÍNICA", "MANIPULACIÓN_COGNITIVA"
    val psychologicalMechanism: String,
    val examplePhrases: List<String>,
    val vulnerabilitiesExploited: String,
    val counterMeasureName: String,
    val surgicalResponse: String,
    val whatNeverToDo: String
)

data class DarkAnalysisResult(
    val detectedTacticName: String,
    val manipulationLevel: String, // "CRÍTICO", "ALTO", "MODERADO"
    val psychologicalObjective: String,
    val victimVulnerability: String,
    val counterStrategy: String,
    val immediateSurgicalResponse: String,
    val longTermAction: String
)

class DarkPsychologyEngine {

    val tacticsCatalog = listOf(
        DarkTactic(
            id = "dt_gaslighting",
            name = "Gaslighting (Distorsión de la Realidad)",
            darkCategory = "MANIPULACIÓN_COGNITIVA",
            psychologicalMechanism = "Siembra dudas sistemáticas sobre la memoria, percepción o cordura de la víctima para hacerla dependiente del criterio del manipulador.",
            examplePhrases = listOf(
                "Estás loco, yo nunca dije eso.",
                "Siempre estás imaginando cosas que no existen.",
                "Tienes mala memoria, pregúntale a cualquiera."
            ),
            vulnerabilitiesExploited = "Deseo de armonía, autoexigencia, miedo a estar equivocado.",
            counterMeasureName = "Anclaje a Hechos & Registro Inmutable",
            surgicalResponse = "«Sé perfectamente lo que escuché y lo que ocurrió. No voy a debatir mi percepción de los hechos. El punto central es este...»",
            whatNeverToDo = "No intentes convencer al manipulador de que tú tienes razón; al discutirle entras en su juego de desgaste mental."
        ),
        DarkTactic(
            id = "dt_intermittent_reinforcement",
            name = "Refuerzo Intermitente (Adicción Dopaminérgica)",
            darkCategory = "MAQUIAVELISMO",
            psychologicalMechanism = "Alterna recompensas emocionales intensas (afecto, atención) con castigos o frialdad impredecible, imitando el mecanismo neurológico de las tragamonedas para generar adicción bioquímica y ansiedad.",
            examplePhrases = listOf(
                "Un día te llena de mensajes de amor y al día siguiente responde con monosílabos fríos sin explicación.",
                "Desaparece por días y reaparece como si nada hubiera pasado con un halago exagerado."
            ),
            vulnerabilitiesExploited = "Necesidad de validación, síndrome del salvador, tolerancia al maltrato por nostalgia del 'inicio perfecto'.",
            counterMeasureName = "Desinversión Espejo & Retirada Emocional",
            surgicalResponse = "«Cuando hay frialdad o silencio, respondo con silencio y reduzco mi inversión a cero. No persigo ni pregunto '¿hice algo malo?'»",
            whatNeverToDo = "Nunca redobles esfuerzos, favores o mensajes cuando la otra persona se aleja sin motivo; eso premia la conducta de desprecio."
        ),
        DarkTactic(
            id = "dt_triangulation",
            name = "Triangulación Maquiavélica",
            darkCategory = "MAQUIAVELISMO",
            psychologicalMechanism = "Introduce a una tercera persona (un pretendiente, un amigo o un ex) para inducir competencia, inseguridad y sensación de escasez artificial.",
            examplePhrases = listOf(
                "Mi ex siempre me llevaba a lugares mejores.",
                "Hay un chico en el trabajo que no para de insistirme en invitarme a cenar.",
                "A mis amigos les pareces un poco aburrido."
            ),
            vulnerabilitiesExploited = "Ego masculino, miedo a ser reemplazado, instinto de competencia descalibrado.",
            counterMeasureName = "Desdén Racional & Libertad Total",
            surgicalResponse = "«Si consideras que esa persona es más compatible contigo, eres libre de salir con ella. No compito con nadie por atención básica.»",
            whatNeverToDo = "Nunca intentes 'demostrar que eres mejor' comprando regalos, peleando o esforzándote más; la triangulación busca que trabajes gratis para su ego."
        ),
        DarkTactic(
            id = "dt_silent_treatment",
            name = "Tratamiento Silencioso (Silent Treatment)",
            darkCategory = "NARCISISMO",
            psychologicalMechanism = "Corta la comunicación abruptamente como castigo emocional para generar angustia, forzando a la víctima a pedir perdón incluso sin haber cometido ninguna falta.",
            examplePhrases = listOf(
                "Ignorar mensajes durante 48 horas tras un desacuerdo.",
                "Caminar al lado en silencio absoluto con lenguaje corporal hostil negándose a hablar."
            ),
            vulnerabilitiesExploited = "Fobia al abandono, hipersensibilidad al rechazo.",
            counterMeasureName = "Inmunidad por Desconexión (Técnica Gray Rock)",
            surgicalResponse = "«Veo que no estás en disposición de conversar con madurez en este momento. Me avisas cuando quieras hablar con respeto.» (Y continúas tu vida con total normalidad).",
            whatNeverToDo = "No envíes 20 mensajes pidiendo perdón ni supliques que te responda; el silencio manipulador solo pierde poder cuando no genera angustia."
        ),
        DarkTactic(
            id = "dt_guilt_induction",
            name = "Chantaje Emocional & Inversión de Culpa (DARVO)",
            darkCategory = "NARCISISMO",
            psychologicalMechanism = "Deny, Attack, and Reverse Victim and Offender. Niega la conducta, ataca a quien confronta y se coloca en el rol de víctima.",
            examplePhrases = listOf(
                "¡Por tu culpa me puse así! Si no fueras tan desconfiado yo no tendría que ocultarte cosas.",
                "Con todo lo que he sufrido en la vida y tú me vienes a reclamar por una tontería."
            ),
            vulnerabilitiesExploited = "Empatía excesiva, sentido exacerbado de culpa.",
            counterMeasureName = "Técnica del Disco Rayado & No JADE",
            surgicalResponse = "«Comprendo tus sentimientos, pero la conducta inaceptable fue [X hecho concreto] y de eso estamos hablando hoy.»",
            whatNeverToDo = "No uses JADE (Justify, Argue, Defend, Explain). No te justifiques ante acusaciones absurdas que buscan desviar el foco del problema."
        ),
        DarkTactic(
            id = "dt_double_bind",
            name = "Doble Vínculo (Trampa Sin Salida)",
            darkCategory = "MANIPULACIÓN_COGNITIVA",
            psychologicalMechanism = "Estructura una demanda contradictoria donde cualquier elección que tomes será calificada como un error o falta de amor.",
            examplePhrases = listOf(
                "Si me compras flores: 'Solo lo haces para compensar'. Si no compras: 'Nunca tienes detalles conmigo'.",
                "Si preguntas: 'Eres un controlador asfixiante'. Si no preguntas: 'No te importo en absoluto'."
            ),
            vulnerabilitiesExploited = "Necesidad de complacer, perfeccionismo relacional.",
            counterMeasureName = "Metacomunicación & Exposición de la Paradoja",
            surgicalResponse = "«Me estás planteando una situación donde cualquier opción que elija será considerada un fallo. No voy a entrar en ese juego de contradicciones.»",
            whatNeverToDo = "No intentes resolver la paradoja; el objetivo del doble vínculo es mantenerte en estado perpetuo de culpa."
        )
    )

    fun analyzeManipulativeInput(text: String): DarkAnalysisResult {
        val lower = text.lowercase()

        return when {
            lower.contains("nunca pasó") || lower.contains("nunca dije") || lower.contains("estás loco") || lower.contains("estas loco") || lower.contains("exageras") || lower.contains("imaginas") -> {
                DarkAnalysisResult(
                    detectedTacticName = "Gaslighting (Invalidación de la Realidad)",
                    manipulationLevel = "ALTO",
                    psychologicalObjective = "Hacerte dudar de tu propio juicio y recuerdos para anular tu capacidad de poner límites.",
                    victimVulnerability = "Inseguridad cognitiva y necesidad de reaseguro externo.",
                    counterStrategy = "No discutir la memoria. Afirmar con calma lo que presenciaste y retirarte de la discusión circular.",
                    immediateSurgicalResponse = "«Sé lo que presencié y no voy a debatir mi percepción. Mantengo mi postura.»",
                    longTermAction = "Registrar acuerdos por escrito y revaluar la permanencia en un vínculo que distorsiona la verdad sistemáticamente."
                )
            }
            lower.contains("si me quisieras") || lower.contains("por tu culpa") || lower.contains("después de todo lo que") || lower.contains("me voy a hacer daño") -> {
                DarkAnalysisResult(
                    detectedTacticName = "Chantaje Emocional & Coerción por Culpa",
                    manipulationLevel = "CRÍTICO",
                    psychologicalObjective = "Extorsionar tu empatía para obligarte a ceder en tus límites personales o tolerar abusos.",
                    victimVulnerability = "Miedo a ser percibido como 'mala persona' o 'egoísta'.",
                    counterStrategy = "Separar tu responsabilidad de las emociones ajenas. Un adulto es responsable de sus propias reacciones.",
                    immediateSurgicalResponse = "«Te quiero, pero no voy a renunciar a mis límites. Eres responsable de tus decisiones.»",
                    longTermAction = "Si hay amenazas de autolesión, avisar a familiares o servicios de emergencia y retirarse inmediatamente (no ser rehén emocional)."
                )
            }
            lower.contains("mi ex") || lower.contains("otro chico") || lower.contains("otro hombre") || lower.contains("me invitan") || lower.contains("amigos dicen") -> {
                DarkAnalysisResult(
                    detectedTacticName = "Triangulación Competitiva Maquiavélica",
                    manipulationLevel = "MODERADO",
                    psychologicalObjective = "Fabricar sensación de escasez y rivalidad para que luches desesperadamente por su atención.",
                    victimVulnerability = "Ego herido y miedo a la pérdida.",
                    counterStrategy = "Desactivar la competencia por completo. Otorgar libertad absoluta para que elija a la otra persona.",
                    immediateSurgicalResponse = "«Si consideras que esa persona es mejor opción para ti, tienes total libertad de explorar esa opción. No compito.»",
                    longTermAction = "Observar si busca provocar celos recurrentemente; de ser así, retirarse por falta de madurez y respeto."
                )
            }
            else -> {
                DarkAnalysisResult(
                    detectedTacticName = "Presión Psicológica / Intento de Control Asimétrico",
                    manipulationLevel = "MODERADO",
                    psychologicalObjective = "Modificar tu conducta o decisiones a través de incomodidad, ambigüedad o tensión inducida.",
                    victimVulnerability = "Tolerancia a la falta de respeto por miedo al conflicto.",
                    counterStrategy = "Aplicar la Técnica de la Piedra Gris (Gray Rock): responder de forma neutra, breve y sin reactividad emocional.",
                    immediateSurgicalResponse = "«Entiendo tu perspectiva. Esta es mi decisión al respecto y no está sujeta a negociación.»",
                    longTermAction = "Mantener una inversión proporcional y no ceder ante tácticas de desgaste emocional."
                )
            }
        }
    }
}
