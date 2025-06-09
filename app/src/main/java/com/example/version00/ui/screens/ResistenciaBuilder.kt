package com.example.version00.data

import com.example.version00.ui.screens.DiaEntrenamiento
import com.example.version00.ui.screens.Ejercicio
import com.example.version00.ui.screens.SemanaEntrenamiento

object ResistenciaBuilder {

    fun build(): List<SemanaEntrenamiento> {
        return listOf(
            crearSemana1(),
            crearSemana2(),
            crearSemana3(),
            crearSemana4()
        )
    }

    private fun crearSemana1(): SemanaEntrenamiento {
        return SemanaEntrenamiento(
            nombre = "Semana 1",
            dias = listOf(
                dia("Día 1", listOf(
                    ejercicio(24, "Buenos días con Barra", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Bíceps femoral" to 55, "Glúteo mayor" to 50)),
                    ejercicio(1, "Abdominales en V con Mancuerna", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Recto abdominal" to 65, "Oblicuo externo" to 45)),
                    ejercicio(68, "Cuarto de Sentadilla con Barra", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Vasto lateral" to 50, "Glúteo mayor" to 25)),
                    ejercicio(50, "Crunch Cruzado", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Oblicuo externo" to 55, "Recto abdominal" to 35))
                )),
                dia("Día 2", listOf(
                    ejercicio(25, "Burpee", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Vasto lateral" to 40, "Recto abdominal" to 15)),
                    ejercicio(1, "Abdominales en V con Mancuerna", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Recto abdominal" to 65, "Oblicuo externo" to 45)),
                    ejercicio(24, "Buenos días con Barra", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Bíceps femoral" to 55, "Glúteo mayor" to 50)),
                    ejercicio(50, "Crunch Cruzado", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Oblicuo externo" to 55, "Recto abdominal" to 35))
                )),
                dia("Día 3", listOf(
                    ejercicio(1, "Abdominales en V con Mancuerna", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Recto abdominal" to 65, "Oblicuo externo" to 45)),
                    ejercicio(50, "Crunch Cruzado", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Oblicuo externo" to 55, "Recto abdominal" to 35)),
                    ejercicio(12, "Aperturas con Mancuernas", "https://fitcron.com/wp-content/uploads/2021/03/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 55, "Deltoides anterior" to 25)),
                    ejercicio(24, "Buenos días con Barra", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Bíceps femoral" to 55, "Glúteo mayor" to 50))
                )),
                dia("Día 4", listOf(
                    ejercicio(1, "Abdominales en V con Mancuerna", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Recto abdominal" to 65, "Oblicuo externo" to 45)),
                    ejercicio(25, "Burpee", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Vasto lateral" to 40, "Recto abdominal" to 15)),
                    ejercicio(50, "Crunch Cruzado", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Oblicuo externo" to 55, "Recto abdominal" to 35)),
                    ejercicio(24, "Buenos días con Barra", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Bíceps femoral" to 55, "Glúteo mayor" to 50))
                )),
                dia("Día 5", listOf(
                    ejercicio(68, "Cuarto de Sentadilla con Barra", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Vasto lateral" to 50, "Glúteo mayor" to 25)),
                    ejercicio(25, "Burpee", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Vasto lateral" to 40, "Recto abdominal" to 15)),
                    ejercicio(1, "Abdominales en V con Mancuerna", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Recto abdominal" to 65, "Oblicuo externo" to 45)),
                    ejercicio(12, "Aperturas con Mancuernas", "https://fitcron.com/wp-content/uploads/2021/03/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 55, "Deltoides anterior" to 25))
                ))
            )
        )
    }

    private fun crearSemana2(): SemanaEntrenamiento {
        return SemanaEntrenamiento(
            nombre = "Semana 2",
            dias = listOf(
                dia("Día 1", listOf(
                    ejercicio(24, "Buenos días con Barra", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Bíceps femoral" to 55, "Glúteo mayor" to 50)),
                    ejercicio(1, "Abdominales en V con Mancuerna", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Recto abdominal" to 65, "Oblicuo externo" to 45)),
                    ejercicio(68, "Cuarto de Sentadilla con Barra", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Vasto lateral" to 50, "Glúteo mayor" to 25)),
                    ejercicio(50, "Crunch Cruzado", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Oblicuo externo" to 55, "Recto abdominal" to 35))
                )),
                dia("Día 2", listOf(
                    ejercicio(1, "Abdominales en V con Mancuerna", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Recto abdominal" to 65, "Oblicuo externo" to 45)),
                    ejercicio(68, "Cuarto de Sentadilla con Barra", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Vasto lateral" to 50, "Glúteo mayor" to 25)),
                    ejercicio(50, "Crunch Cruzado", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Oblicuo externo" to 55, "Recto abdominal" to 35)),
                    ejercicio(25, "Burpee", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Vasto lateral" to 40, "Recto abdominal" to 15))
                )),
                dia("Día 3", listOf(
                    ejercicio(68, "Cuarto de Sentadilla con Barra", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Vasto lateral" to 50, "Glúteo mayor" to 25)),
                    ejercicio(50, "Crunch Cruzado", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Oblicuo externo" to 55, "Recto abdominal" to 35)),
                    ejercicio(25, "Burpee", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Vasto lateral" to 40, "Recto abdominal" to 15)),
                    ejercicio(12, "Aperturas con Mancuernas", "https://fitcron.com/wp-content/uploads/2021/03/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 55, "Deltoides anterior" to 25))
                )),
                dia("Día 4", listOf(
                    ejercicio(50, "Crunch Cruzado", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Oblicuo externo" to 55, "Recto abdominal" to 35)),
                    ejercicio(25, "Burpee", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Vasto lateral" to 40, "Recto abdominal" to 15)),
                    ejercicio(12, "Aperturas con Mancuernas", "https://fitcron.com/wp-content/uploads/2021/03/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 55, "Deltoides anterior" to 25))
                )),
                dia("Día 5", listOf(
                    ejercicio(25, "Burpee", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Vasto lateral" to 40, "Recto abdominal" to 15)),
                    ejercicio(12, "Aperturas con Mancuernas", "https://fitcron.com/wp-content/uploads/2021/03/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 55, "Deltoides anterior" to 25))
                ))
            )
        )
    }

    private fun crearSemana3(): SemanaEntrenamiento {
        return SemanaEntrenamiento(
            nombre = "Semana 3",
            dias = listOf(
                dia("Día 1", listOf(
                    ejercicio(24, "Buenos días con Barra", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Bíceps femoral" to 55, "Glúteo mayor" to 50)),
                    ejercicio(1, "Abdominales en V con Mancuerna", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Recto abdominal" to 65, "Oblicuo externo" to 45)),
                    ejercicio(68, "Cuarto de Sentadilla con Barra", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Vasto lateral" to 50, "Glúteo mayor" to 25)),
                    ejercicio(50, "Crunch Cruzado", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Oblicuo externo" to 55, "Recto abdominal" to 35))
                )),
                dia("Día 2", listOf(
                    ejercicio(1, "Abdominales en V con Mancuerna", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Recto abdominal" to 65, "Oblicuo externo" to 45)),
                    ejercicio(68, "Cuarto de Sentadilla con Barra", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Vasto lateral" to 50, "Glúteo mayor" to 25)),
                    ejercicio(50, "Crunch Cruzado", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Oblicuo externo" to 55, "Recto abdominal" to 35)),
                    ejercicio(25, "Burpee", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Vasto lateral" to 40, "Recto abdominal" to 15))
                )),
                dia("Día 3", listOf(
                    ejercicio(68, "Cuarto de Sentadilla con Barra", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Vasto lateral" to 50, "Glúteo mayor" to 25)),
                    ejercicio(50, "Crunch Cruzado", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Oblicuo externo" to 55, "Recto abdominal" to 35)),
                    ejercicio(25, "Burpee", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Vasto lateral" to 40, "Recto abdominal" to 15)),
                    ejercicio(12, "Aperturas con Mancuernas", "https://fitcron.com/wp-content/uploads/2021/03/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 55, "Deltoides anterior" to 25))
                )),
                dia("Día 4", listOf(
                    ejercicio(50, "Crunch Cruzado", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Oblicuo externo" to 55, "Recto abdominal" to 35)),
                    ejercicio(25, "Burpee", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Vasto lateral" to 40, "Recto abdominal" to 15)),
                    ejercicio(12, "Aperturas con Mancuernas", "https://fitcron.com/wp-content/uploads/2021/03/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 55, "Deltoides anterior" to 25))
                )),
                dia("Día 5", listOf(
                    ejercicio(25, "Burpee", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Vasto lateral" to 40, "Recto abdominal" to 15)),
                    ejercicio(12, "Aperturas con Mancuernas", "https://fitcron.com/wp-content/uploads/2021/03/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 55, "Deltoides anterior" to 25))
                ))
            )
        )
    }

    private fun crearSemana4(): SemanaEntrenamiento {
        return SemanaEntrenamiento(
            nombre = "Semana 4",
            dias = listOf(
                dia("Día 1", listOf(
                    ejercicio(24, "Buenos días con Barra", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Bíceps femoral" to 55, "Glúteo mayor" to 50)),
                    ejercicio(1, "Abdominales en V con Mancuerna", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Recto abdominal" to 65, "Oblicuo externo" to 45)),
                    ejercicio(68, "Cuarto de Sentadilla con Barra", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Vasto lateral" to 50, "Glúteo mayor" to 25)),
                    ejercicio(50, "Crunch Cruzado", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Oblicuo externo" to 55, "Recto abdominal" to 35))
                )),
                dia("Día 2", listOf(
                    ejercicio(1, "Abdominales en V con Mancuerna", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Recto abdominal" to 65, "Oblicuo externo" to 45)),
                    ejercicio(68, "Cuarto de Sentadilla con Barra", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Vasto lateral" to 50, "Glúteo mayor" to 25)),
                    ejercicio(50, "Crunch Cruzado", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Oblicuo externo" to 55, "Recto abdominal" to 35)),
                    ejercicio(25, "Burpee", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Vasto lateral" to 40, "Recto abdominal" to 15))
                )),
                dia("Día 3", listOf(
                    ejercicio(68, "Cuarto de Sentadilla con Barra", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Vasto lateral" to 50, "Glúteo mayor" to 25)),
                    ejercicio(50, "Crunch Cruzado", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Oblicuo externo" to 55, "Recto abdominal" to 35)),
                    ejercicio(25, "Burpee", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Vasto lateral" to 40, "Recto abdominal" to 15)),
                    ejercicio(12, "Aperturas con Mancuernas", "https://fitcron.com/wp-content/uploads/2021/03/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 55, "Deltoides anterior" to 25))
                )),
                dia("Día 4", listOf(
                    ejercicio(50, "Crunch Cruzado", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Oblicuo externo" to 55, "Recto abdominal" to 35)),
                    ejercicio(25, "Burpee", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Vasto lateral" to 40, "Recto abdominal" to 15)),
                    ejercicio(12, "Aperturas con Mancuernas", "https://fitcron.com/wp-content/uploads/2021/03/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 55, "Deltoides anterior" to 25))
                )),
                dia("Día 5", listOf(
                    ejercicio(25, "Burpee", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Vasto lateral" to 40, "Recto abdominal" to 15)),
                    ejercicio(12, "Aperturas con Mancuernas", "https://fitcron.com/wp-content/uploads/2021/03/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 55, "Deltoides anterior" to 25))
                ))
            )
        )
    }

    private fun dia(nombre: String, ejercicios: List<Ejercicio>): DiaEntrenamiento {
        return DiaEntrenamiento(nombre = nombre, ejercicios = ejercicios)
    }

    private fun ejercicio(
        id: Int,
        nombre: String,
        url: String,
        activacionSimple: Map<String, Int>
    ): Ejercicio {
        val activacionAnidada = mapOf("musculos" to activacionSimple) // Envolvemos con clave fija

        return Ejercicio(
            id = id,
            nombre = nombre,
            url = url,
            activacion = activacionAnidada,
            series = 2,
            repeticiones = 15,
            rir = 3
        )
    }

}

object HipertrofiaBuilder {

    fun build(): List<SemanaEntrenamiento> {
        return listOf(
            crearSemana1(),
            crearSemana2(),
            crearSemana3(),
            crearSemana4()
        )
    }

    private fun crearSemana1() = SemanaEntrenamiento(
        nombre = "Hipertrofia Semana 1",
        dias = listOf(
            dia("Día 1", listOf(
                ejercicio(101, "Sentadilla profunda con barra", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Cuádriceps" to 70, "Glúteo mayor" to 60)),
                ejercicio(102, "Press de banca", "https://fitcron.com/wp-content/uploads/2021/04/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 80, "Tríceps" to 55)),
                ejercicio(103, "Remo con barra", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Dorsal ancho" to 75, "Bíceps" to 60))
            )),
            dia("Día 2", listOf(
                ejercicio(104, "Press militar con mancuernas", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Deltoides anterior" to 70, "Tríceps" to 50)),
                ejercicio(105, "Curl bíceps con barra", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Bíceps" to 85)),
                ejercicio(106, "Elevaciones laterales", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Deltoides lateral" to 60))
            )),
            dia("Día 3", listOf(
                ejercicio(107, "Peso muerto rumano", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Isquiotibiales" to 75, "Glúteo mayor" to 65)),
                ejercicio(108, "Fondos en paralelas", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Pectoral mayor" to 70, "Tríceps" to 60)),
                ejercicio(109, "Remo con mancuernas", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Dorsal ancho" to 70, "Bíceps" to 65))
            )),
            dia("Día 4", listOf(
                ejercicio(110, "Press Arnold", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Deltoides anterior" to 75, "Tríceps" to 55)),
                ejercicio(111, "Curl martillo", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Bíceps braquial" to 80)),
                ejercicio(112, "Face pull", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Deltoides posterior" to 65))
            )),
            dia("Día 5", listOf(
                ejercicio(113, "Extensiones de tríceps", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Tríceps" to 75)),
                ejercicio(114, "Curl femoral tumbado", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Isquiotibiales" to 70)),
                ejercicio(115, "Abdominales bicicleta", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Recto abdominal" to 60, "Oblicuo externo" to 50))
            ))
        )
    )

    private fun crearSemana2() = SemanaEntrenamiento(
        nombre = "Hipertrofia Semana 2",
        dias = listOf(
            dia("Día 1", listOf(
                ejercicio(116, "Sentadilla búlgara", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Cuádriceps" to 72, "Glúteo mayor" to 62)),
                ejercicio(117, "Press inclinado con mancuernas", "https://fitcron.com/wp-content/uploads/2021/04/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 78, "Tríceps" to 57)),
                ejercicio(118, "Remo con barra T", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Dorsal ancho" to 73, "Bíceps" to 62))
            )),
            dia("Día 2", listOf(
                ejercicio(119, "Press militar con barra", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Deltoides anterior" to 73, "Tríceps" to 53)),
                ejercicio(120, "Curl bíceps alterno", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Bíceps" to 87)),
                ejercicio(121, "Elevaciones posteriores", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Deltoides posterior" to 67))
            )),
            dia("Día 3", listOf(
                ejercicio(122, "Peso muerto rumano", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Isquiotibiales" to 77, "Glúteo mayor" to 67)),
                ejercicio(123, "Fondos en paralelas", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Pectoral mayor" to 72, "Tríceps" to 62)),
                ejercicio(124, "Remo con mancuernas", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Dorsal ancho" to 72, "Bíceps" to 67))
            )),
            dia("Día 4", listOf(
                ejercicio(125, "Press Arnold", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Deltoides anterior" to 77, "Tríceps" to 57)),
                ejercicio(126, "Curl martillo", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Bíceps braquial" to 82)),
                ejercicio(127, "Face pull", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Deltoides posterior" to 67))
            )),
            dia("Día 5", listOf(
                ejercicio(128, "Extensiones de tríceps", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Tríceps" to 77)),
                ejercicio(129, "Curl femoral tumbado", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Isquiotibiales" to 72)),
                ejercicio(130, "Abdominales bicicleta", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Recto abdominal" to 62, "Oblicuo externo" to 52))
            ))
        )
    )

    private fun crearSemana3() = SemanaEntrenamiento(
        nombre = "Hipertrofia Semana 3",
        dias = listOf(
            dia("Día 1", listOf(
                ejercicio(131, "Sentadilla frontal", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Cuádriceps" to 75, "Glúteo mayor" to 65)),
                ejercicio(132, "Press de banca plano", "https://fitcron.com/wp-content/uploads/2021/04/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 83, "Tríceps" to 57)),
                ejercicio(133, "Remo con barra", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Dorsal ancho" to 75, "Bíceps" to 63))
            )),
            dia("Día 2", listOf(
                ejercicio(134, "Press militar con mancuernas", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Deltoides anterior" to 75, "Tríceps" to 55)),
                ejercicio(135, "Curl bíceps con barra", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Bíceps" to 85)),
                ejercicio(136, "Elevaciones laterales", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Deltoides lateral" to 62))
            )),
            dia("Día 3", listOf(
                ejercicio(137, "Peso muerto rumano", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Isquiotibiales" to 78, "Glúteo mayor" to 68)),
                ejercicio(138, "Fondos en paralelas", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Pectoral mayor" to 73, "Tríceps" to 63)),
                ejercicio(139, "Remo con mancuernas", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Dorsal ancho" to 73, "Bíceps" to 68))
            )),
            dia("Día 4", listOf(
                ejercicio(140, "Press Arnold", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Deltoides anterior" to 78, "Tríceps" to 58)),
                ejercicio(141, "Curl martillo", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Bíceps braquial" to 83)),
                ejercicio(142, "Face pull", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Deltoides posterior" to 68))
            )),
            dia("Día 5", listOf(
                ejercicio(143, "Extensiones de tríceps", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Tríceps" to 78)),
                ejercicio(144, "Curl femoral tumbado", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Isquiotibiales" to 73)),
                ejercicio(145, "Abdominales bicicleta", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Recto abdominal" to 63, "Oblicuo externo" to 53))
            ))
        )
    )

    private fun crearSemana4() = SemanaEntrenamiento(
        nombre = "Hipertrofia Semana 4",
        dias = listOf(
            dia("Día 1", listOf(
                ejercicio(146, "Sentadilla búlgara", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Cuádriceps" to 74, "Glúteo mayor" to 64)),
                ejercicio(147, "Press inclinado con mancuernas", "https://fitcron.com/wp-content/uploads/2021/04/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 80, "Tríceps" to 60)),
                ejercicio(148, "Remo con barra T", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Dorsal ancho" to 74, "Bíceps" to 66))
            )),
            dia("Día 2", listOf(
                ejercicio(149, "Press militar con barra", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Deltoides anterior" to 74, "Tríceps" to 55)),
                ejercicio(150, "Curl bíceps alterno", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Bíceps" to 88)),
                ejercicio(151, "Elevaciones posteriores", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Deltoides posterior" to 66))
            )),
            dia("Día 3", listOf(
                ejercicio(152, "Peso muerto rumano", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Isquiotibiales" to 79, "Glúteo mayor" to 69)),
                ejercicio(153, "Fondos en paralelas", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Pectoral mayor" to 74, "Tríceps" to 64)),
                ejercicio(154, "Remo con mancuernas", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Dorsal ancho" to 74, "Bíceps" to 69))
            )),
            dia("Día 4", listOf(
                ejercicio(155, "Press Arnold", "https://fitcron.com/wp-content/uploads/2021/04/02621301-Cross-Body-Crunch_waist_720.gif", mapOf("Deltoides anterior" to 79, "Tríceps" to 59)),
                ejercicio(156, "Curl martillo", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Bíceps braquial" to 84)),
                ejercicio(157, "Face pull", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Deltoides posterior" to 69))
            )),
            dia("Día 5", listOf(
                ejercicio(158, "Extensiones de tríceps", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Tríceps" to 79)),
                ejercicio(159, "Curl femoral tumbado", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Isquiotibiales" to 74)),
                ejercicio(160, "Abdominales bicicleta", "https://fitcron.com/wp-content/uploads/2021/04/11601301-Burpee_Cardio_720.gif", mapOf("Recto abdominal" to 64, "Oblicuo externo" to 54))
            ))
        )
    )

    private fun dia(nombre: String, ejercicios: List<Ejercicio>) = DiaEntrenamiento(nombre = nombre, ejercicios = ejercicios)

    private fun ejercicio(
        id: Int,
        nombre: String,
        url: String,
        activacionSimple: Map<String, Int>
    ): Ejercicio {
        val activacionAnidada = mapOf("musculos" to activacionSimple)
        return Ejercicio(
            id = id,
            nombre = nombre,
            url = url,
            activacion = activacionAnidada,
            series = 3,
            repeticiones = 10,
            rir = 2
        )
    }
}

object FuerzaBuilder {

    fun build(): List<SemanaEntrenamiento> {
        return listOf(
            crearSemana1(),
            crearSemana2(),
            crearSemana3(),
            crearSemana4()
        )
    }

    private fun crearSemana1() = SemanaEntrenamiento(
        nombre = "Fuerza Semana 1",
        dias = listOf(
            dia("Día 1", listOf(
                ejercicio(201, "Peso muerto convencional", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Isquiotibiales" to 80, "Glúteo mayor" to 70)),
                ejercicio(202, "Press de banca pesado", "https://fitcron.com/wp-content/uploads/2021/04/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 85, "Tríceps" to 65))
            )),
            dia("Día 2", listOf(
                ejercicio(203, "Sentadilla frontal", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Cuádriceps" to 75, "Glúteo mayor" to 60)),
                ejercicio(204, "Dominadas con peso", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Dorsal ancho" to 85, "Bíceps" to 70))
            )),
            dia("Día 3", listOf(
                ejercicio(205, "Press militar con barra pesada", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Deltoides anterior" to 80, "Tríceps" to 70)),
                ejercicio(206, "Remo Pendlay", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Dorsal ancho" to 80, "Bíceps" to 75))
            )),
            dia("Día 4", listOf(
                ejercicio(207, "Sentadilla trasera pesada", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Cuádriceps" to 80, "Glúteo mayor" to 70)),
                ejercicio(208, "Dominadas lastradas", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Dorsal ancho" to 90, "Bíceps" to 80))
            )),
            dia("Día 5", listOf(
                ejercicio(209, "Peso muerto sumo", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Isquiotibiales" to 85, "Glúteo mayor" to 75)),
                ejercicio(210, "Press banca con pausa", "https://fitcron.com/wp-content/uploads/2021/04/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 88, "Tríceps" to 68))
            ))
        )
    )

    private fun crearSemana2() = SemanaEntrenamiento(
        nombre = "Fuerza Semana 2",
        dias = listOf(
            dia("Día 1", listOf(
                ejercicio(211, "Peso muerto convencional", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Isquiotibiales" to 82, "Glúteo mayor" to 72)),
                ejercicio(212, "Press de banca pesado", "https://fitcron.com/wp-content/uploads/2021/04/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 87, "Tríceps" to 66))
            )),
            dia("Día 2", listOf(
                ejercicio(213, "Sentadilla frontal", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Cuádriceps" to 78, "Glúteo mayor" to 62)),
                ejercicio(214, "Dominadas con peso", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Dorsal ancho" to 87, "Bíceps" to 72))
            )),
            dia("Día 3", listOf(
                ejercicio(215, "Press militar con barra pesada", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Deltoides anterior" to 82, "Tríceps" to 72)),
                ejercicio(216, "Remo Pendlay", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Dorsal ancho" to 82, "Bíceps" to 77))
            )),
            dia("Día 4", listOf(
                ejercicio(217, "Sentadilla trasera pesada", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Cuádriceps" to 82, "Glúteo mayor" to 72)),
                ejercicio(218, "Dominadas lastradas", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Dorsal ancho" to 92, "Bíceps" to 82))
            )),
            dia("Día 5", listOf(
                ejercicio(219, "Peso muerto sumo", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Isquiotibiales" to 87, "Glúteo mayor" to 77)),
                ejercicio(220, "Press banca con pausa", "https://fitcron.com/wp-content/uploads/2021/04/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 90, "Tríceps" to 70))
            ))
        )
    )

    private fun crearSemana3() = SemanaEntrenamiento(
        nombre = "Fuerza Semana 3",
        dias = listOf(
            dia("Día 1", listOf(
                ejercicio(221, "Peso muerto convencional", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Isquiotibiales" to 83, "Glúteo mayor" to 73)),
                ejercicio(222, "Press de banca pesado", "https://fitcron.com/wp-content/uploads/2021/04/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 89, "Tríceps" to 67))
            )),
            dia("Día 2", listOf(
                ejercicio(223, "Sentadilla frontal", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Cuádriceps" to 80, "Glúteo mayor" to 65)),
                ejercicio(224, "Dominadas con peso", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Dorsal ancho" to 89, "Bíceps" to 74))
            )),
            dia("Día 3", listOf(
                ejercicio(225, "Press militar con barra pesada", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Deltoides anterior" to 83, "Tríceps" to 73)),
                ejercicio(226, "Remo Pendlay", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Dorsal ancho" to 83, "Bíceps" to 78))
            )),
            dia("Día 4", listOf(
                ejercicio(227, "Sentadilla trasera pesada", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Cuádriceps" to 83, "Glúteo mayor" to 73)),
                ejercicio(228, "Dominadas lastradas", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Dorsal ancho" to 93, "Bíceps" to 83))
            )),
            dia("Día 5", listOf(
                ejercicio(229, "Peso muerto sumo", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Isquiotibiales" to 88, "Glúteo mayor" to 78)),
                ejercicio(230, "Press banca con pausa", "https://fitcron.com/wp-content/uploads/2021/04/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 91, "Tríceps" to 71))
            ))
        )
    )

    private fun crearSemana4() = SemanaEntrenamiento(
        nombre = "Fuerza Semana 4",
        dias = listOf(
            dia("Día 1", listOf(
                ejercicio(231, "Peso muerto convencional", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Isquiotibiales" to 84, "Glúteo mayor" to 74)),
                ejercicio(232, "Press de banca pesado", "https://fitcron.com/wp-content/uploads/2021/04/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 90, "Tríceps" to 68))
            )),
            dia("Día 2", listOf(
                ejercicio(233, "Sentadilla frontal", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Cuádriceps" to 81, "Glúteo mayor" to 66)),
                ejercicio(234, "Dominadas con peso", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Dorsal ancho" to 90, "Bíceps" to 75))
            )),
            dia("Día 3", listOf(
                ejercicio(235, "Press militar con barra pesada", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Deltoides anterior" to 84, "Tríceps" to 74)),
                ejercicio(236, "Remo Pendlay", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Dorsal ancho" to 84, "Bíceps" to 79))
            )),
            dia("Día 4", listOf(
                ejercicio(237, "Sentadilla trasera pesada", "https://fitcron.com/wp-content/uploads/2021/04/04131301-Dumbbell-Squat_Hips_720.gif", mapOf("Cuádriceps" to 84, "Glúteo mayor" to 74)),
                ejercicio(238, "Dominadas lastradas", "https://fitcron.com/wp-content/uploads/2021/04/33361301-Dumbbell-V-up_Waist_720.gif", mapOf("Dorsal ancho" to 94, "Bíceps" to 84))
            )),
            dia("Día 5", listOf(
                ejercicio(239, "Peso muerto sumo", "https://fitcron.com/wp-content/uploads/2021/04/00441301-Barbell-Good-Morning_Thighs_720.gif", mapOf("Isquiotibiales" to 89, "Glúteo mayor" to 79)),
                ejercicio(240, "Press banca con pausa", "https://fitcron.com/wp-content/uploads/2021/04/03081301-Dumbbell-Fly_Chest-FIX_720.gif", mapOf("Pectoral mayor" to 92, "Tríceps" to 72))
            ))
        )
    )

    private fun dia(nombre: String, ejercicios: List<Ejercicio>) = DiaEntrenamiento(nombre = nombre, ejercicios = ejercicios)

    private fun ejercicio(
        id: Int,
        nombre: String,
        url: String,
        activacionSimple: Map<String, Int>
    ): Ejercicio {
        val activacionAnidada = mapOf("musculos" to activacionSimple)
        return Ejercicio(
            id = id,
            nombre = nombre,
            url = url,
            activacion = activacionAnidada,
            series = 4,
            repeticiones = 6,
            rir = 1
        )
    }
}
