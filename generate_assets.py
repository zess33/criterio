# -*- coding: utf-8 -*-
import json
import os

assets_dir = "/Users/pedrourielhuertaplantillas/.gemini/antigravity/scratch/criterio-android/app/src/main/assets"
os.makedirs(assets_dir, exist_ok=True)

# 1. MODULES AND LESSONS DATA
modules = [
    {
        "id": "mod_0",
        "levelIndex": 0,
        "title": "Nivel 0 — Fundamentos y Presencia",
        "description": "Bases esenciales de autoestima, presentación personal, lenguaje corporal, contacto visual, modulación vocal y superación del miedo inicial.",
        "category": "FUNDAMENTOS",
        "requiredScore": 0,
        "estimatedMinutes": 45,
        "lessons": [
            {
                "id": "les_0_1",
                "orderIndex": 1,
                "title": "Autoestima Real vs Validación Externa",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "La autoestima no es sentirse superior a los demás, sino no necesitar la aprobación ajena para validar el propio valor. Cuando buscas desesperadamente agradar, actúas desde la necesidad y la sumisión.",
                "examples": [
                    "Expresar tus gustos con tranquilidad aunque difieran de los de tu interlocutora.",
                    "Poder decir 'no me gusta ese plan' sin miedo a que la persona se aleje."
                ],
                "counterExamples": [
                    "Cambiar de opinión inmediatamente cuando la otra persona muestra desacuerdo.",
                    "Comprar regalos caros en la primera cita para 'ganarte' su interés."
                ],
                "commonErrors": [
                    "Confundir arrogancia con seguridad.",
                    "Creer que la aprobación femenina te otorgará valor como hombre."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Cuál es la diferencia clave entre seguridad genuina y necesidad de aprobación?",
                    "options": [
                        "La seguridad genuina no requiere que la otra persona valide tus opiniones o decisiones.",
                        "La seguridad genuina consiste en imponer tu punto de vista en cualquier discusión.",
                        "La necesidad de aprobación es buena porque demuestra humildad y cortesía."
                    ],
                    "correctIndex": 0,
                    "explanation": "La seguridad radica en la congruencia interna; no depende de la reacción del entorno."
                })
            },
            {
                "id": "les_0_2",
                "orderIndex": 2,
                "title": "Lenguaje Corporal, Contacto Visual y Voz",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "La comunicación no verbal comunica tu nivel de calma y presencia. El contacto visual sostenido (sin mirar fijamente de forma agresiva), una postura abierta y un tono de voz pausado transmiten tranquilidad.",
                "examples": [
                    "Mantener contacto visual cómodo con pausas naturales.",
                    "Hombros relajados, respiración diafragmática y hablar sin prisa."
                ],
                "counterExamples": [
                    "Mirar al suelo o esquivar la mirada constantemente al hablar.",
                    "Cruzar los brazos y mirar el reloj cada 30 segundos."
                ],
                "commonErrors": [
                    "Forzar una postura rígida que parece antinatural.",
                    "Hablar demasiado rápido creyendo que así no aburres."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Qué comunica hablar a un ritmo pausado y con respiración relajada?",
                    "options": [
                        "Falta de energía y desinterés.",
                        "Control emocional, claridad cognitiva y ausencia de urgencia ansiosa.",
                        "Indiferencia calculada."
                    ],
                    "correctIndex": 1,
                    "explanation": "La calma en la voz denota que estás cómodo en tu propia piel y no temes ser escuchado."
                })
            },
            {
                "id": "les_0_3",
                "orderIndex": 3,
                "title": "Miedo al Rechazo y Desensibilización",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "El rechazo no es un veredicto sobre tu valor como ser humano; es simplemente una incompatibilidad contextual de intereses o momentos vitales.",
                "examples": [
                    "Aceptar una negativa con una sonrisa y continuar tu día con naturalidad.",
                    "Entender que una persona tiene derecho a no sentirse atraída sin que eso signifique que hiciste algo mal."
                ],
                "counterExamples": [
                    "Tomar el rechazo como un ataque personal o una prueba de insuficiencia.",
                    "Insistir o pedir explicaciones tras un 'no' claro."
                ],
                "commonErrors": [
                    "Evitar interactuar por completo para no experimentar incomodidad.",
                    "Guardar rencor hacia las personas que no corresponden tu interés."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Por qué un rechazo no es un veredicto sobre tu valor personal?",
                    "options": [
                        "Porque las personas eligen según sus propias preferencias, momentos y compatibilidad, no según un ranking universal.",
                        "Porque quien te rechaza siempre se equivocó.",
                        "Porque el rechazo es sólo una técnica de coqueteo."
                    ],
                    "correctIndex": 0,
                    "explanation": "Las preferencias individuales son subjetivas y contextuales; no existe una jerarquía objetiva de valor personal."
                })
            }
        ]
    },
    {
        "id": "mod_1",
        "levelIndex": 1,
        "title": "Nivel 1 — Dinámicas de Conversación",
        "evidenceLevel": "HIGH_EVIDENCE",
        "description": "Aperturas contextuales, preguntas abiertas, escucha activa, evitar interrogatorios, storytelling y saber cuándo cerrar una interacción.",
        "category": "CONVERSACION",
        "requiredScore": 100,
        "estimatedMinutes": 50,
        "lessons": [
            {
                "id": "les_1_1",
                "orderIndex": 1,
                "title": "Aperturas Contextuales vs Frases Prehechas",
                "evidenceLevel": "MODERATE_EVIDENCE",
                "conceptExplanation": "Las mejores conversaciones surgen de observar el entorno compartido en tiempo real, no de memorizar frases enlatadas o trucos de internet.",
                "examples": [
                    "Comentar con naturalidad sobre el libro que está leyendo o la música del lugar.",
                    "Hacer una observación graciosa y ligera sobre la situación del momento."
                ],
                "counterExamples": [
                    "Usar piropos genéricos copiados de videos de TikTok.",
                    "Hacer cumplidos forzados sobre el físico en los primeros 3 segundos."
                ],
                "commonErrors": [
                    "Pensar que necesitas una frase mágica para que alguien te preste atención.",
                    "Ignorar el lenguaje corporal de incomodidad de la otra persona."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Por qué son más efectivas las observaciones contextuales que las frases prefabricadas?",
                    "options": [
                        "Porque son auténticas, adaptadas al momento y no generan la sensación de estar siendo evaluado con un guion.",
                        "Porque confunden a la otra persona.",
                        "Porque son más difíciles de memorizar."
                    ],
                    "correctIndex": 0,
                    "explanation": "La naturalidad reduce las defensas sociales y abre un espacio de interacción espontánea."
                })
            },
            {
                "id": "les_1_2",
                "orderIndex": 2,
                "title": "Escucha Activa vs Interrogatorio",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "Una conversación fluida no es una batería de preguntas (¿dónde trabajas? ¿dónde vives? ¿tienes hermanos?). Es un intercambio de reflexiones, anécdotas y emociones.",
                "examples": [
                    "Preguntar: '¿Qué fue lo que más te apasionó de ese proyecto?' y construir sobre su respuesta.",
                    "Compartir una breve experiencia propia antes de invitar a la otra persona a comentar."
                ],
                "counterExamples": [
                    "Disparar 5 preguntas seguidas sin profundizar en ninguna respuesta.",
                    "Estar pensando en lo siguiente que vas a decir en lugar de escuchar lo que te están diciendo."
                ],
                "commonErrors": [
                    "Monopolizar la conversación hablando solo de tus logros.",
                    "Hacer preguntas tipo encuesta que se responden con sí o no."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Cómo evitas que una conversación se sienta como un interrogatorio policial?",
                    "options": [
                        "Alternando preguntas abiertas con aportes personales e interpretaciones curiosas sobre lo que ella comparte.",
                        "Haciendo más de 20 preguntas cerradas en 5 minutos.",
                        "No haciendo ninguna pregunta y hablando sólo tú."
                    ],
                    "correctIndex": 0,
                    "explanation": "La reciprocidad conversacional combina indagación genuina con vulnerabilidad y aportes propios."
                })
            },
            {
                "id": "les_1_3",
                "orderIndex": 3,
                "title": "El Arte de Concluir una Conversación a Tiempo",
                "evidenceLevel": "MODERATE_EVIDENCE",
                "conceptExplanation": "Saber retirarse en un punto alto de la interacción genera respeto por el tiempo de ambos y evita que la conversación se vuelva pesada o incómoda.",
                "examples": [
                    "'Ha sido un gusto charlar contigo, me tengo que reunir con unos amigos. Si te apetece, intercambiamos contactos y seguimos otro día.'",
                    "Notar señales de prisa o cansancio y facilitar una salida cordial sin resentimiento."
                ],
                "counterExamples": [
                    "Alargar la interacción a la fuerza cuando la otra persona ya dio señales de querer irse.",
                    "Quedarse en silencio incómodo esperando que ella termine la conversación."
                ],
                "commonErrors": [
                    "Sentir que terminar la conversación primero es grosero.",
                    "Pedir el contacto de forma insistente cuando la charla no fluyó."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Cuál es la ventaja de cerrar una conversación en su momento óptimo?",
                    "options": [
                        "Demuestra respeto mutuo, calibración social y deja una impresión positiva y liviana.",
                        "Obliga a la otra persona a perseguirte desesperadamente.",
                        "Ninguna, siempre hay que hablar hasta que te echen."
                    ],
                    "correctIndex": 0,
                    "explanation": "Cerrar con elegancia denota alta inteligencia social y autonomía."
                })
            }
        ]
    },
    {
        "id": "mod_2",
        "levelIndex": 2,
        "title": "Nivel 2 — Confianza, Límites y Autenticidad",
        "evidenceLevel": "HIGH_EVIDENCE",
        "description": "Establecimiento de límites saludables, honestidad emocional, cómo erradicar la necesidad de aparentar y construcción de autoestima sólida.",
        "category": "CONFIANZA",
        "requiredScore": 200,
        "estimatedMinutes": 45,
        "lessons": [
            {
                "id": "les_2_1",
                "orderIndex": 1,
                "title": "Límites Personales: La Piedra Angular del Respeto",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "No puedes esperar que otros respeten tus límites si tú mismo estás dispuesto a traicionarlos para agradar. Un 'no' oportuno protege tu dignidad y establece bases sanas.",
                "examples": [
                    "Expresar con serenidad: 'No me siento cómodo con que canceles planes a última hora sin avisar.'",
                    "Rechazar prestar dinero o hacer favores desproporcionados a personas recién conocidas."
                ],
                "counterExamples": [
                    "Aceptar tratos irrespetuosos por miedo a que la persona se enoje o se vaya.",
                    "Pedir disculpas por expresar tus necesidades básicas."
                ],
                "commonErrors": [
                    "Poner límites con agresividad o resentimiento acumulado.",
                    "Creer que ser 'bueno' significa ser complaciente en todo."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Qué ocurre cuando una persona nunca pone límites por miedo a perder a alguien?",
                    "options": [
                        "Fomenta dinámicas de desequilibrio, resentimiento interno y pérdida progresiva de respeto mutuo.",
                        "Garantiza una relación armoniosa y duradera para siempre.",
                        "Demuestra un amor incondicional saludable."
                    ],
                    "correctIndex": 0,
                    "explanation": "La complacencia crónica genera resentimiento y deteriora la autoestima."
                })
            },
            {
                "id": "les_2_2",
                "orderIndex": 2,
                "title": "Autenticidad vs Máscaras Sociales",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "Fingir ser un 'hombre exitoso / rudo / perfecto' es agotador e insostenible. La verdadera atracción a largo plazo solo puede construirse sobre la autenticidad.",
                "examples": [
                    "Admitir con humor que no sabes nada de un tema en lugar de inventar.",
                    "Mostrar tus verdaderos intereses aunque no sean considerados 'populares'."
                ],
                "counterExamples": [
                    "Alquilar autos caros o presumir marcas para impresionar a una cita.",
                    "Fingir estar de acuerdo con todo lo que la otra persona dice."
                ],
                "commonErrors": [
                    "Confundir autenticidad con falta de filtro social o grosería.",
                    "Creer que tu versión real no es suficiente."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Por qué es contraproducente utilizar una máscara de personaje?",
                    "options": [
                        "Porque atraes a personas compatibles con la máscara y no contigo, lo que garantiza el fracaso a largo plazo.",
                        "Porque es más divertido ser otra persona.",
                        "Porque nadie nota las máscaras."
                    ],
                    "correctIndex": 0,
                    "explanation": "La compatibilidad real requiere conocer a la persona real."
                })
            }
        ]
    },
    {
        "id": "mod_3",
        "levelIndex": 3,
        "title": "Nivel 3 — Atracción, Coqueteo y Reciprocidad",
        "evidenceLevel": "MODERATE_EVIDENCE",
        "description": "Demostrar interés genuino, tensión sana, cumplidos bien calibrados, señales de interés y desinterés, y saber cuándo avanzar o detenerse.",
        "category": "ATRACCION",
        "requiredScore": 300,
        "estimatedMinutes": 55,
        "lessons": [
            {
                "id": "les_3_1",
                "orderIndex": 1,
                "title": "Claridad y Demostración de Interés sin Acoso",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "Demostrar interés romántico de forma respetuosa y clara es atractivo. La ambigüedad eterna ('amigo oculto') genera confusión y frustración para ambas partes.",
                "examples": [
                    "'Me encanta conversar contigo, pero me gustaría invitarte a una cita para conocernos mejor.'",
                    "Hacer un cumplido sobre su sentido del humor o su perspectiva con sinceridad."
                ],
                "counterExamples": [
                    "Hacer favores durante meses esperando que 'mágicamente' se dé cuenta de que te gusta.",
                    "Enviar mensajes sexualmente explícitos sin haber establecido confianza ni consentimiento previo."
                ],
                "commonErrors": [
                    "Pensar que mostrar interés te hace vulnerable o 'débil'.",
                    "Confundir persistencia no solicitada con perseverancia romántica."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Cuál es la forma más sana de comunicar atracción inicial?",
                    "options": [
                        "De forma directa, educada, sin presiones y respetando plenamente la respuesta de la otra persona.",
                        "Con indirectas ambiguas para nunca arriesgarte al rechazo.",
                        "Ignorándola por completo para que se sienta insegura."
                    ],
                    "correctIndex": 0,
                    "explanation": "La asertividad romántica es clara y respeta la autonomía del otro."
                })
            },
            {
                "id": "les_3_2",
                "orderIndex": 2,
                "title": "Lectura de Reciprocidad vs Señales Aisladas",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "Ninguna señal aislada (una sonrisa, un emoji, mirar el cabello) demuestra interés por sí sola. La única métrica confiable es el patrón consistente de reciprocidad en tiempo, iniciativa y esfuerzo.",
                "examples": [
                    "Si ella no puede un día, propone una fecha alternativa: 'Hoy no puedo, ¿qué tal el jueves?'.",
                    "Inicia conversaciones y muestra curiosidad genuina por tus planes."
                ],
                "counterExamples": [
                    "Asumir que porque una cajera sonrió amablemente está coqueteando contigo.",
                    "Creer que un 'jajaja' en un chat significa que está enamorada."
                ],
                "commonErrors": [
                    "Analizar obsesivamente el micro-comportamiento en lugar del panorama general.",
                    "Ignorar el desinterés evidente esperando que 'cambie de opinión'."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Qué constituye una prueba real de interés en una dinámica interpersonal?",
                    "options": [
                        "Un patrón observable de reciprocidad, iniciativa y disponibilidad mutua en el tiempo.",
                        "Un mensaje de WhatsApp enviado a las 3 AM.",
                        "Que te mire durante 2 segundos en una reunión."
                    ],
                    "correctIndex": 0,
                    "explanation": "El interés genuino se traduce en hechos y acciones sostenidas, no en micro-gestos aislados."
                })
            }
        ]
    },
    {
        "id": "mod_4",
        "levelIndex": 4,
        "title": "Nivel 4 — Citas, Finanzas y Consentimiento",
        "evidenceLevel": "HIGH_EVIDENCE",
        "description": "Logística de primeras citas, dinámica económica (50/50 y reciprocidad), contacto físico respetuoso, consentimiento activo y lectura de química.",
        "category": "CITAS",
        "requiredScore": 400,
        "estimatedMinutes": 60,
        "lessons": [
            {
                "id": "les_4_1",
                "orderIndex": 1,
                "title": "Diseño de la Primera Cita: Ambiente y Logística",
                "evidenceLevel": "MODERATE_EVIDENCE",
                "conceptExplanation": "Una buena primera cita debe priorizar la comodidad, la conversación sin ruidos excesivos y una logística sencilla que permita extender o concluir el plan con facilidad.",
                "examples": [
                    "Un café en una zona agradable o una caminata por un parque o museo.",
                    "Un lugar que ambos puedan pagar cómodamente y donde se pueda conversar mirándose."
                ],
                "counterExamples": [
                    "Una cena de 5 platos en un restaurante ultra formal donde el ambiente es rígido y costoso.",
                    "Ir al cine donde pasan 2 horas a oscuras sin poder hablar."
                ],
                "commonErrors": [
                    "Gastar más de lo que tu presupuesto permite para aparentar.",
                    "No tener un plan B en caso de que el lugar esté cerrado o lleno."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Por qué un plan dinámico y conversacional es mejor para una primera cita que una cena costosa?",
                    "options": [
                        "Porque reduce la presión para ambos, facilita el diálogo y permite evaluar la química real con naturalidad.",
                        "Porque es la única forma de no gastar dinero.",
                        "Porque a las personas no les gusta la buena comida."
                    ],
                    "correctIndex": 0,
                    "explanation": "El objetivo de la primera cita es conocerse mutuamente, no deslumbrar materialmente."
                })
            },
            {
                "id": "les_4_2",
                "orderIndex": 2,
                "title": "Dinero, Quién Paga y Reciprocidad Real",
                "evidenceLevel": "MODERATE_EVIDENCE",
                "conceptExplanation": "El debate del '50/50 vs quien invita' no debe verse como una regla moral rígida. La clave es la reciprocidad: valorar la consideración, el agradecimiento y el equilibrio general de esfuerzos.",
                "examples": [
                    "Si tú invitaste la cena, que la otra persona ofrezca el café o postre, o agradezca con genuina cortesía.",
                    "Hablar con madurez sobre el presupuesto en citas posteriores."
                ],
                "counterExamples": [
                    "Exigir una división matemática al centavo si tú propusiste un restaurante carísimo.",
                    "Asumir que pagar una cuenta te otorga 'derechos' sobre el tiempo o el cuerpo de otra persona."
                ],
                "commonErrors": [
                    "Etiquetar a alguien de 'aprovechada' por un solo malentendido en la cuenta.",
                    "Tolerar una actitud de derecho absoluto ('tú debes pagarme todo siempre')."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Qué diferencia al 50/50 matemático de la reciprocidad real?",
                    "options": [
                        "La reciprocidad evalúa el equilibrio integral de consideración, esfuerzo y agradecimiento mutuo según el contexto.",
                        "El 50/50 matemático es la única forma de respeto que existe.",
                        "La reciprocidad sólo cuenta si se transfiere dinero por transferencia bancaria."
                    ],
                    "correctIndex": 0,
                    "explanation": "La reciprocidad es una actitud de mutuo cuidado y contribución, no una simple factura contable."
                })
            },
            {
                "id": "les_4_3",
                "orderIndex": 3,
                "title": "Consentimiento Activo y Contacto Progresivo",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "El consentimiento no es un trámite burocrático; es una sintonía continua basada en el entusiasmo y la comodidad mutua. Ante la menor duda o incomodidad, la regla es pausar.",
                "examples": [
                    "Prestar atención a si la otra persona se inclina hacia ti o se aleja ante el contacto físico.",
                    "Preguntar con calidez y naturalidad: '¿Estás cómoda?' o '¿Quieres que nos besemos?'."
                ],
                "counterExamples": [
                    "Intentar forzar un beso cuando la otra persona mantiene distancia o cruza los brazos.",
                    "Asumir que el silencio o la falta de resistencia física equivale a consentimiento entusiasta."
                ],
                "commonErrors": [
                    "Creer que pedir consentimiento 'mata la pasión' (en realidad genera seguridad y confianza).",
                    "Presionar emocionalmente tras una negativa."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Qué debes hacer si notas que tu cita se tensa o retrocede ante un gesto de cercanía física?",
                    "options": [
                        "Retirar suavemente el contacto, dar espacio y continuar conversando con total tranquilidad y respeto.",
                        "Insistir más fuerte para demostrar 'dominancia'.",
                        "Ofenderte y reclamarle por qué se alejó."
                    ],
                    "correctIndex": 0,
                    "explanation": "Respetar el espacio ajeno demuestra madurez emocional y construye un entorno seguro."
                })
            }
        ]
    },
    {
        "id": "mod_5",
        "levelIndex": 5,
        "title": "Nivel 5 — Relaciones, Conflictos y Proyectos",
        "evidenceLevel": "HIGH_EVIDENCE",
        "description": "Construcción de acuerdos claros, gestión de celos, comunicación no violenta, resolución de conflictos, finanzas y alineación de proyectos de vida.",
        "category": "RELACIONES",
        "requiredScore": 500,
        "estimatedMinutes": 60,
        "lessons": [
            {
                "id": "les_5_1",
                "orderIndex": 1,
                "title": "Gestión de Conflictos: Atacar el Problema, no a la Persona",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "En una relación sana, los desacuerdos no son una competencia de 'quién gana', sino un trabajo en equipo contra una dificultad compartida. Evita los 4 jinetes del apocalipsis relacional (crítica destructiva, desprecio, actitud defensiva y evasión).",
                "examples": [
                    "Usar enunciados en primera persona: 'Me sentí abrumado cuando...' en lugar de 'Tú siempre arruinas todo...'",
                    "Tomar una pausa de 20 minutos para calmar el ritmo cardíaco antes de continuar una discusión acalorada."
                ],
                "counterExamples": [
                    "Sacar a relucir errores de hace tres años en medio de una discusión actual.",
                    "Aplicar la ley del hielo (silent treatment) como castigo emocional."
                ],
                "commonErrors": [
                    "Querer 'ganar' la discusión en lugar de entender el origen del malestar.",
                    "Guardar rencores sin hablarlos hasta explotar."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Cuál es la estrategia más eficaz ante una discusión de pareja que sube de tono?",
                    "options": [
                        "Pausar la conversación de mutuo acuerdo para regular las emociones y retomarla desde la empatía y la resolución de problemas.",
                        "Gritar más fuerte para que tu argumento sea escuchado.",
                        "Bloquear a la persona en redes sociales durante dos días."
                    ],
                    "correctIndex": 0,
                    "explanation": "La autorregulación emocional previene el desbordamiento fisiológico que destruye la comunicación."
                })
            },
            {
                "id": "les_5_2",
                "orderIndex": 2,
                "title": "Celos: Origen, Manejo y Distinción de Hechos",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "Los celos son una señal de miedo a la pérdida o inseguridad personal. Deben gestionarse comunicando vulnerabilidad ('me sentí inseguro') y no mediante conductas de control (revisar celulares, prohibir amistades).",
                "examples": [
                    "Reconocer tu propia inseguridad y hablar de tus límites con serenidad.",
                    "Diferenciar entre un comportamiento objetivamente ambiguo de la pareja y una proyección de tus propios traumas pasados."
                ],
                "counterExamples": [
                    "Exigir las contraseñas de las redes sociales de tu pareja.",
                    "Hacer escenas públicas de reclamo sin evidencia objetiva."
                ],
                "commonErrors": [
                    "Pensar que los celos son una 'prueba de amor'.",
                    "Ignorar banderas rojas reales por miedo a parecer celoso."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Cómo se distingue un límite saludable de una conducta de control celosa?",
                    "options": [
                        "Un límite define lo que TÚ aceptas o no en tu vida; el control busca coartar la libertad y autonomía de la OTRA persona.",
                        "No hay diferencia, son lo mismo.",
                        "El control siempre es necesario para que no te sean infieles."
                    ],
                    "correctIndex": 0,
                    "explanation": "Tus límites regulan tus decisiones de permanencia; el control intenta vigilar a la otra persona."
                })
            }
        ]
    },
    {
        "id": "mod_6",
        "levelIndex": 6,
        "title": "Nivel 6 — Psicología Práctica y Apego",
        "evidenceLevel": "HIGH_EVIDENCE",
        "description": "Estilos de apego (seguro, ansioso, evitativo), mecanismos de defensa, proyección psicológica y regulación emocional sin caer en diagnósticos amateurs.",
        "category": "PSICOLOGIA",
        "requiredScore": 600,
        "estimatedMinutes": 55,
        "lessons": [
            {
                "id": "les_6_1",
                "orderIndex": 1,
                "title": "Teoría del Apego en Adultos",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "Los estilos de apego describen cómo respondemos ante la intimidad y la amenaza de abandono. Conocer tu estilo te permite regular tus reacciones automáticas sin etiquetar patológicamente a los demás.",
                "examples": [
                    "Apego seguro: Comunica necesidades claramente y tolera la independencia mutua.",
                    "Apego ansioso: Tiende a buscar reaseguro constante y sobreinterpretar silencios.",
                    "Apego evitativo: Se repliega o distancia ante la vulnerabilidad emocional."
                ],
                "counterExamples": [
                    "Usar la etiqueta 'eres evitativa' como insulto o excusa para justificar el acoso.",
                    "Diagnosticar trastornos de personalidad en personas con las que saliste dos semanas."
                ],
                "commonErrors": [
                    "Creer que los estilos de apego son inmutables.",
                    "Caer en la trampa del baile 'ansioso-evitativo' sin poner límites."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Cuál es el propósito principal de comprender la teoría del apego?",
                    "options": [
                        "Identificar tus propios disparadores emocionales para responder con mayor madurez y elegir parejas disponibles.",
                        "Tener argumentos para diagnosticar y culpar a tus ex parejas.",
                        "Aprender a manipular la mente de las personas."
                    ],
                    "correctIndex": 0,
                    "explanation": "El autoconocimiento es la base para transitar hacia un apego seguro y relaciones estables."
                })
            }
        ]
    },
    {
        "id": "mod_7",
        "levelIndex": 7,
        "title": "Nivel 7 — Pensamiento Crítico y Sesgos",
        "evidenceLevel": "HIGH_EVIDENCE",
        "description": "Correlación vs causalidad, sesgo de confirmación, efecto halo, falacia del francotirador, anécdota vs evidencia y superación de cámaras de eco.",
        "category": "PENSAMIENTO_CRITICO",
        "requiredScore": 700,
        "estimatedMinutes": 50,
        "lessons": [
            {
                "id": "les_7_1",
                "orderIndex": 1,
                "title": "Sesgo de Confirmación y Falacias de Generalización",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "El cerebro tiende a buscar y recordar únicamente la información que confirma sus creencias previas, ignorando la evidencia contraria. Decir 'todas las mujeres son X' porque tuviste 2 malas experiencias es una falacia de generalización apresurada.",
                "examples": [
                    "Reconocer: 'Mi experiencia negativa con una persona no define al 50% de la población mundial.'",
                    "Buscar activamente explicaciones alternativas antes de concluir que alguien tiene malas intenciones."
                ],
                "counterExamples": [
                    "Consumir exclusivamente videos de redes sociales que alimentan el resentimiento de género.",
                    "Ignorar las conductas amables de alguien y enfocarse sólo en un error para justificar tu desconfianza."
                ],
                "commonErrors": [
                    "Confundir 'muchos likes en un video de TikTok' con evidencia científica.",
                    "Creer que tu experiencia personal representa la verdad estadística global."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Por qué es irracional afirmar 'todas las personas son infieles' basándose en una anécdota personal?",
                    "options": [
                        "Porque una muestra de tamaño N=1 o N=2 carece de representatividad estadística y está sesgada por selección personal.",
                        "Porque las personas nunca mienten.",
                        "Porque las anécdotas siempre son más válidas que los estudios rigurosos."
                    ],
                    "correctIndex": 0,
                    "explanation": "La representatividad y el tamaño de muestra son esenciales para cualquier inferencia válida."
                })
            },
            {
                "id": "les_7_2",
                "orderIndex": 2,
                "title": "Efecto Halo y Sobrevaloración Inicial",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "El efecto halo ocurre cuando atribuimos virtudes morales o de personalidad (inteligencia, bondad, empatía) a alguien simplemente porque nos resulta físicamente atractiva.",
                "examples": [
                    "Evaluar la compatibilidad y el carácter de una persona a través de sus acciones a lo largo de meses, no de su apariencia.",
                    "Recordar que el atractivo físico no garantiza madurez emocional ni reciprocidad."
                ],
                "counterExamples": [
                    "Justificar faltas de respeto o malos tratos porque la persona es muy atractiva.",
                    "Pensar que alguien 'debe ser perfecta' antes de haber conversado con ella más de una hora."
                ],
                "commonErrors": [
                    "Idealizar a personas que apenas conoces.",
                    "Proyectar tus deseos sobre la realidad de la otra persona."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Cómo se neutraliza el Efecto Halo en el dating?",
                    "options": [
                        "Observando la consistencia entre sus palabras y acciones a través del tiempo, sin asumir cualidades que no ha demostrado.",
                        "Tratando mal a las personas atractivas.",
                        "Fijándose únicamente en su signo del zodíaco."
                    ],
                    "correctIndex": 0,
                    "explanation": "El tiempo y la observación objetiva revelan el verdadero carácter y valores."
                })
            }
        ]
    },
    {
        "id": "mod_8",
        "levelIndex": 8,
        "title": "Nivel 8 — Estadística, Muestreo e Incertidumbre",
        "evidenceLevel": "HIGH_EVIDENCE",
        "description": "Tasas base, sesgo de selección en apps de citas, tamaños muestrales, variabilidad humana y cómo tolerar la incertidumbre social sin angustia.",
        "category": "ESTADISTICA",
        "requiredScore": 800,
        "estimatedMinutes": 50,
        "lessons": [
            {
                "id": "les_8_1",
                "orderIndex": 1,
                "title": "Sesgo de Selección en Redes y Dating Apps",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "Los usuarios más activos o extremos en redes sociales no representan al promedio de la población. Las apps y los algoritmos premian el contenido polarizante (rage bait), creando una ilusión de conflicto generalizado.",
                "examples": [
                    "Comprender que los videos virales de 'hombres de alto valor' o 'mujeres exigiendo sueldos millonarios' son creados para monetizar clics y no reflejan la vida real.",
                    "Saber que conocer personas en clubes o apps atrae un subconjunto con dinámicas muy distintas a las de grupos de voluntariado o clubes de lectura."
                ],
                "counterExamples": [
                    "Creer que el comportamiento de los influencers representa a las mujeres reales de tu comunidad.",
                    "Pensar que todas las interacciones humanas son transaccionales porque así se muestran en internet."
                ],
                "commonErrors": [
                    "Extrapolar el algoritmo de tu feed a la realidad cotidiana.",
                    "Olvidar que los algoritmos optimizan para indignación y tiempo en pantalla."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Por qué el contenido viral sobre relaciones en redes suele ser engañoso?",
                    "options": [
                        "Porque está diseñado con sesgo de selección e incentivos de polarización para generar reacciones emocionales intensas y viralidad.",
                        "Porque todos los influencers son científicos certificados.",
                        "Porque refleja exactamente lo que piensa toda la humanidad."
                    ],
                    "correctIndex": 0,
                    "explanation": "El incentivo de las plataformas es retener atención, no educar con rigor científico."
                })
            }
        ]
    },
    {
        "id": "mod_9",
        "levelIndex": 9,
        "title": "Nivel 9 — Razonamiento Bayesiano y Toma de Decisiones",
        "evidenceLevel": "HIGH_EVIDENCE",
        "description": "Actualización de probabilidades ante nueva evidencia (Prior -> Likelihood -> Posterior), árboles de decisión y manejo racional de la incertidumbre.",
        "category": "BAYES_DECISIONES",
        "requiredScore": 900,
        "estimatedMinutes": 60,
        "lessons": [
            {
                "id": "les_9_1",
                "orderIndex": 1,
                "title": "Pensamiento Bayesiano en la Vida Cotidiana",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "El Teorema de Bayes enseña que no debes saltar a conclusiones extremas (0% o 100%) ante una sola señal. Comienzas con una probabilidad previa razonable y la actualizas gradualmente conforme recibes datos verificables.",
                "examples": [
                    "Prior: Una persona recién conocida tiene una probabilidad moderada de ser compatible contigo.",
                    "Nueva evidencia: Cancela sin reagendar. Actualizas tu estimación hacia menor compatibilidad.",
                    "Nueva evidencia: Te propone otro día con entusiasmo y llega puntual. Actualizas hacia mayor compatibilidad."
                ],
                "counterExamples": [
                    "Pasar de 'es el amor de mi vida' a 'es una manipuladora malvada' por un solo retraso de 15 minutos.",
                    "Ignorar 10 señales de incompatibilidad porque tuviste un momento bonito al inicio."
                ],
                "commonErrors": [
                    "Inventar porcentajes matemáticos exactos cuando lo que se requiere es ajuste cualitativo de expectativas.",
                    "Aferrarse a la creencia inicial a pesar de la evidencia abrumadora en contra."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Qué nos enseña la actualización bayesiana frente a una señal ambigua?",
                    "options": [
                        "Ajustar moderadamente nuestras hipótesis sin saltar a conclusiones absolutas, manteniendo la mente abierta hasta recolectar más datos.",
                        "Asumir de inmediato el peor escenario posible para proteger nuestro ego.",
                        "Ignorar la realidad y actuar como si nada hubiera pasado."
                    ],
                    "correctIndex": 0,
                    "explanation": "El pensamiento bayesiano calibra la confianza en función de la calidad y cantidad de evidencia disponible."
                })
            }
        ]
    },
    {
        "id": "mod_10",
        "levelIndex": 10,
        "title": "Nivel 10 — Dinámicas Complejas, Manipulación y Teoría de Juegos",
        "evidenceLevel": "HIGH_EVIDENCE",
        "description": "Detección de patrones manipuladores (gaslighting, refuerzo intermitente), teoría de juegos (cooperación, reciprocidad, incentivos) y resolución asertiva.",
        "category": "DINAMICAS_COMPLEJAS",
        "requiredScore": 1000,
        "estimatedMinutes": 65,
        "lessons": [
            {
                "id": "les_10_1",
                "orderIndex": 1,
                "title": "Diferenciar Desacuerdo Legítimo de Manipulación Emocional",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "Tener opiniones diferentes o límites incompatibles es normal. La manipulación real implica distorsionar la realidad (gaslighting), generar culpa para vulnerar límites o usar refuerzo intermitente para crear adicción.",
                "examples": [
                    "Desacuerdo: 'No me gusta ese tipo de música y prefiero no ir al concierto contigo.' (Límite sano)",
                    "Manipulación: 'Si realmente me amaras, dejarías a todos tus amigos por mí.' (Chantaje emocional)"
                ],
                "counterExamples": [
                    "Llamar 'tóxica' a cualquier persona que no esté de acuerdo con tus ideas.",
                    "Confundir la torpeza comunicativa puntual con una estrategia maquiavélica de control."
                ],
                "commonErrors": [
                    "Quedarse en una relación esperando 'rescatar' o 'cambiar' a una persona manipuladora.",
                    "Creer que tolerar malos tratos te hace más noble o leal."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Cuál es la respuesta adecuada ante un patrón reiterado de chantaje o invalidación emocional?",
                    "options": [
                        "Establecer límites firmes y claros, y si la conducta persiste, retirarse de la relación con dignidad.",
                        "Intentar convencerla con argumentos interminables para que cambie de personalidad.",
                        "Pagarle con la misma moneda aplicando técnicas de manipulación."
                    ],
                    "correctIndex": 0,
                    "explanation": "La mejor protección contra dinámicas tóxicas es la capacidad de retirarte cuando se vulneran tus límites."
                })
            },
            {
                "id": "les_10_2",
                "orderIndex": 2,
                "title": "Teoría de Juegos: Cooperación y Ojo por Ojo Generoso (Tit-for-Tat)",
                "evidenceLevel": "HIGH_EVIDENCE",
                "conceptExplanation": "En relaciones repetidas, la estrategia matemáticamente más exitosa es comenzar cooperando, ser recíproco ante la cooperación del otro, y desinvertir calmadamente si hay deserción (falta de reciprocidad), pero perdonar si la cooperación se reanuda.",
                "examples": [
                    "Invertir tiempo y atención en alguien que corresponde con la misma dedicación.",
                    "Si notas que la otra persona deja de responder y no invierte, reduces tu inversión sin rencores."
                ],
                "counterExamples": [
                    "Dar el 100% incondicionalmente a alguien que aporta el 0% (auto-sacrificio tóxico).",
                    "Buscar venganza o resentimiento cuando alguien no puede salir contigo."
                ],
                "commonErrors": [
                    "Jugar juegos de poder o pretender desinterés fingido.",
                    "No saber retirarse ante la falta crónica de cooperación mutua."
                ],
                "quizDataJson": json.dumps({
                    "question": "¿Por qué la estrategia de reciprocidad generosa supera tanto a la sumisión como al cinismo?",
                    "options": [
                        "Porque premia la cooperación mutua y protege contra el aprovechamiento sin caer en la hostilidad destructiva.",
                        "Porque permite ganar todas las discusiones sin esfuerzo.",
                        "Porque obliga a la otra persona a hacer lo que tú quieras."
                    ],
                    "correctIndex": 0,
                    "explanation": "Fomenta relaciones simétricas basadas en incentivos alineados de respeto mutuo."
                })
            }
        ]
    }
]

# 2. SCENARIOS DATA FOR SIMULATOR
scenarios = [
    {
        "id": "scen_cafe",
        "title": "Encuentro en una Cafetería",
        "category": "ENTORNO_CASUAL",
        "difficulty": "PRINCIPIANTE",
        "context": "Estás en una cafetería tranquila. En la mesa de al lado, una mujer está leyendo un libro sobre filosofía que tú conoces bien. Ella bebe su café tranquilamente y parece relajada.",
        "personaName": "Sofía",
        "personaRole": "Lectora habitual, amable pero reservada",
        "personaOpening": "Disculpa, ¿sabes si hay algún enchufe cerca de esta mesa?",
        "initialPrompt": "Sofía te pregunta con cortesía por un enchufe mientras sostiene su cargador.",
        "scoringRubric": {
            "clarityWeight": 0.25,
            "pressureWeight": 0.35,
            "contextReadingWeight": 0.40
        }
    },
    {
        "id": "scen_dating_app",
        "title": "Manejo de Mensajes Secos en Dating Apps",
        "category": "DIGITAL_MESSAGING",
        "difficulty": "INTERMEDIO",
        "context": "Hiciste match en una app hace dos días. Tuvieron una charla divertida sobre viajes, pero su última respuesta fue simplemente: 'Jajaja sí'.",
        "personaName": "Renata",
        "personaRole": "Diseñadora, ocupada, interesada si la charla no es cliché",
        "personaOpening": "Jajaja sí",
        "initialPrompt": "Renata ha respondido con un mensaje corto y seco a tu anécdota anterior.",
        "scoringRubric": {
            "clarityWeight": 0.30,
            "pressureWeight": 0.40,
            "contextReadingWeight": 0.30
        }
    },
    {
        "id": "scen_first_date",
        "title": "Primera Cita: Conversación Balanceada",
        "category": "CITAS",
        "difficulty": "INTERMEDIO",
        "context": "Están en un bar tranquilo en su primera cita. Acaban de pedir sus bebidas. La conversación recién comienza.",
        "personaName": "Mariana",
        "personaRole": "Curiosa, evalúa autenticidad y buen sentido del humor",
        "personaOpening": "Y bueno, cuéntame algo que no esté en tu perfil... ¿qué te apasiona hacer cuando nadie te está mirando?",
        "initialPrompt": "Mariana te mira sonriendo y te hace una pregunta abierta e interesante.",
        "scoringRubric": {
            "clarityWeight": 0.30,
            "pressureWeight": 0.30,
            "contextReadingWeight": 0.40
        }
    },
    {
        "id": "scen_rejection",
        "title": "Recepción Elegante de un Rechazo",
        "category": "MANEJO_RECHAZO",
        "difficulty": "AVANZADO",
        "context": "Han salido dos veces. Tras la segunda cita, le propones un tercer plan y ella te envía este mensaje:",
        "personaName": "Camila",
        "personaRole": "Honesta y respetuosa",
        "personaOpening": "Hola, la pasé súper bien en nuestras salidas, pero siendo honesta siento que conectamos más como amigos y no estoy buscando algo romántico.",
        "initialPrompt": "Camila te ha comunicado con claridad que no siente química romántica.",
        "scoringRubric": {
            "clarityWeight": 0.35,
            "pressureWeight": 0.40,
            "contextReadingWeight": 0.25
        }
    },
    {
        "id": "scen_gym",
        "title": "Gimnasio: Calibración y Espacio Personal",
        "category": "ENTORNO_SENSIBLE",
        "difficulty": "AVANZADO",
        "context": "Estás en la zona de pesas. Una chica está haciendo su rutina con audífonos puestos y concentrada en sus series.",
        "personaName": "Lucía",
        "personaRole": "Atleta enfocada, no le gusta ser interrumpida durante sus series",
        "personaOpening": "(Está descansando entre series mirando su cronómetro)",
        "initialPrompt": "Considera el contexto: tiene audífonos y cronómetro. ¿Cuál es la forma más respetuosa y calibrada de interactuar si necesitas compartir la máquina?",
        "scoringRubric": {
            "clarityWeight": 0.30,
            "pressureWeight": 0.40,
            "contextReadingWeight": 0.30
        }
    }
]

# 3. TEST ME SITUATIONAL QUESTIONS
test_questions = [
    {
        "id": "tq_1",
        "scenario": "Una persona con la que sales hace 3 semanas no contesta tu mensaje durante 7 horas un día laboral. ¿Cuál es la interpretación racional?",
        "options": [
            "Está ocupada con su trabajo o vida personal; no hay evidencia suficiente para asumir desinterés ni juego psicológico.",
            "Definitivamente está jugando a 'hacerse la difícil' y debo dejar de escribirle por 3 días para enseñarle una lección.",
            "Está saliendo con otra persona y ya no le importas en absoluto."
        ],
        "correctIndex": 0,
        "cognitivePrinciple": "Diferenciación de Hechos vs Interpretaciones y Evitación de Lectura de Mente.",
        "explanation": "El retraso aislado de horas en un día laboral carece de significancia estadística sin un patrón sostenido."
    },
    {
        "id": "tq_2",
        "scenario": "Le propones una cita para el sábado a una chica y te responde: 'El sábado no puedo porque tengo un compromiso familiar'. No ofrece una fecha alternativa. ¿Qué haces?",
        "options": [
            "Aceptas amablemente con un 'Sin problema, que disfrutes el compromiso' y dejas que la iniciativa de proponer la siguiente fecha recaiga en ella.",
            "Le preguntas inmediatamente: '¿Y el domingo? ¿O el próximo martes? ¿O cuándo puedes?' para insistir.",
            "Le reclamas que siempre pone excusas."
        ],
        "correctIndex": 0,
        "cognitivePrinciple": "Evaluación de Reciprocidad y Respeto por la Autonomía.",
        "explanation": "Cuando alguien rechaza un plan sin ofrecer alternativa, la respuesta calibrada es no presionar y observar si existe iniciativa posterior."
    },
    {
        "id": "tq_3",
        "scenario": "Un amigo te dice: 'Vi en TikTok que si una mujer te toca el brazo significa que el 90% de las veces quiere acostarse contigo'. ¿Qué falla lógica hay aquí?",
        "options": [
            "Falacia de estadística inventada, sesgo de confirmación y simplificación absurda del comportamiento humano.",
            "Ninguna, TikTok tiene estudios científicos avalados por psicólogos.",
            "La estadística es correcta siempre que el toque dure más de 2 segundos."
        ],
        "correctIndex": 0,
        "cognitivePrinciple": "Pensamiento Crítico y Detección de Pseudociencia Social.",
        "explanation": "No existen estadísticas mágicas ni conductas universales que demuestren atracción inequívoca."
    },
    {
        "id": "tq_4",
        "scenario": "En una cita, la otra persona empieza a hablar despectivamente de todos sus ex novios llamándolos 'locos y monstruos'. ¿Qué hipótesis es más prudente considerar?",
        "options": [
            "Podría indicar un patrón de falta de autocrítica y dificultad para asumir responsabilidad en sus relaciones pasadas.",
            "Demuestra que ella es una víctima inocente y que tú debes salvarla.",
            "Es una prueba de que ella es la persona más sincera del mundo."
        ],
        "correctIndex": 0,
        "cognitivePrinciple": "Evaluación de Patrones de Responsabilidad Personal.",
        "explanation": "Cuando todos los terceros son catalogados como culpables absolutos, suele existir una externalización sistemática de la culpa."
    }
]

# 4. ADVICE DATABASE (DESMITIFICADOR DE CONSEJOS DE INTERNET)
advice_database = [
    {
        "id": "adv_1",
        "claim": "El hombre siempre debe pagar el 100% de todas las citas porque eso demuestra que es un 'proveedor de alto valor'.",
        "originSource": "Creadores de contenido tradicionalistas / High Value Coaches",
        "analysis": {
            "meaning": "Plantea que la valía masculina y el cortejo están determinados exclusivamente por la solvencia financiera y la disposición a absorber el gasto total de la dinámica.",
            "availableEvidence": "MODERATE_EVIDENCE: En culturas tradicionales el rol de proveedor está internalizado, pero estudios contemporáneos demuestran que las relaciones con reciprocidad y acuerdos mutuos tienen mayor estabilidad y satisfacción.",
            "potentialBiases": "Sesgo de género tradicional, visión transaccional de las relaciones humanas y confusión entre solvencia y madurez emocional.",
            "whatMayBeTrue": "La generosidad y el detalle en una primera invitación son socialmente valorados como un gesto de cortesía.",
            "whatIsExaggeration": "Afirmar que quien no paga siempre es 'poco hombre' o que pagar garantiza el respeto y la lealtad de la otra persona.",
            "whatIsUnfounded": "La creencia de que el dinero sustituye a la compatibilidad emocional, el respeto y la buena comunicación.",
            "conclusion": "Pagar o invitar es un gesto libre de cortesía, no un contrato de compra-venta de afecto. La reciprocidad y el agradecimiento mutuo son mucho más predictivos del éxito que una regla financiera rígida."
        }
    },
    {
        "id": "adv_2",
        "claim": "Tarda el doble de tiempo en responder sus mensajes para que se obsesione contigo.",
        "originSource": "Dating coaches de manipulación / RedPill / PUA",
        "analysis": {
            "meaning": "Sugiere utilizar el tiempo de respuesta como una herramienta de manipulación emocional para generar incertidumbre y ansiedad en la otra persona.",
            "availableEvidence": "HIGH_EVIDENCE: La psicología demuestra que el refuerzo intermitente genera respuestas ansiosas en personas con apego inseguro, pero destruye la confianza y ahuyenta a las personas con apego seguro y autoestima sana.",
            "potentialBiases": "Pensamiento manipulador, suposición de que todas las personas reaccionan con obsesión ante el desinterés fingido.",
            "whatMayBeTrue": "No estar pegado al teléfono las 24 horas porque tienes una vida y responsabilidades propias es sano y atractivo.",
            "whatIsExaggeration": "Calcular cronométricamente los minutos para enviar un mensaje como si fuera una estrategia militar.",
            "whatIsUnfounded": "Creer que la manipulación de tiempos construye relaciones estables y sinceras a largo plazo.",
            "conclusion": "Responde con naturalidad cuando tengas tiempo y ganas. Tener una vida ocupada real es maduro; fingir desinterés para manipular es infantil."
        }
    },
    {
        "id": "adv_3",
        "claim": "Si una mujer no te responde en 5 minutos en Instagram es porque tiene 100 hombres más y ya perdiste tu oportunidad.",
        "originSource": "Foros de resentimiento y redes sociales (Rage bait)",
        "analysis": {
            "meaning": "Generalización catastrófica que asume que el tiempo de respuesta en redes refleja una competencia hipergámica constante y malintencionada.",
            "availableEvidence": "LIMITED_EVIDENCE: Las personas utilizan las redes con patrones sumamente heterogéneos según su trabajo, hábitos de atención y vida privada.",
            "potentialBiases": "Paranoia, pensamiento dicotómico (todo o nada), lectura de mente y sesgo de catastrofismo.",
            "whatMayBeTrue": "Las personas muy solicitadas pueden recibir muchos mensajes y demorar en ver notificaciones de desconocidos.",
            "whatIsExaggeration": "Asumir que un retraso equivale a un rechazo categórico o a una conspiración en tu contra.",
            "whatIsUnfounded": "La idea de que todas las personas viven pendientes exclusivamente de los mensajes directos de redes.",
            "conclusion": "No proyectes tus inseguridades en los tiempos de conexión ajenos. Si la interacción fluye con respeto en el mundo real, los tiempos de Instagram son irrelevantes."
        }
    }
]

# Write to files
with open(os.path.join(assets_dir, "modules_data.json"), "w", encoding="utf-8") as f:
    json.dump(modules, f, ensure_ascii=False, indent=2)

with open(os.path.join(assets_dir, "scenarios_data.json"), "w", encoding="utf-8") as f:
    json.dump(scenarios, f, ensure_ascii=False, indent=2)

with open(os.path.join(assets_dir, "test_questions_data.json"), "w", encoding="utf-8") as f:
    json.dump(test_questions, f, ensure_ascii=False, indent=2)

with open(os.path.join(assets_dir, "advice_database.json"), "w", encoding="utf-8") as f:
    json.dump(advice_database, f, ensure_ascii=False, indent=2)

print("Assets successfully generated!")
