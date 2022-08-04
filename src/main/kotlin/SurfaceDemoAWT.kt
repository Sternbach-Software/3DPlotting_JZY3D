import org.jzy3d.analysis.AWTAbstractAnalysis
import kotlin.Throws
import java.lang.Exception
import kotlin.jvm.JvmStatic
import org.jzy3d.plot3d.builder.Func3D
import org.jzy3d.plot3d.builder.SurfaceBuilder
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid
import org.jzy3d.colors.ColorMapper
import org.jzy3d.colors.colormaps.ColorMapRainbow
import org.jzy3d.chart.factories.IChartFactory
import org.jzy3d.chart.factories.AWTChartFactory
import org.jzy3d.colors.Color
import org.jzy3d.colors.colormaps.ColorMapGrayscale
import org.jzy3d.colors.colormaps.ColorMapHotCold
import org.jzy3d.colors.colormaps.IColorMap
import org.jzy3d.maths.Range
import org.jzy3d.plot3d.primitives.Shape
import org.jzy3d.plot3d.rendering.canvas.Quality
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class SurfaceDemoAWT : AWTAbstractAnalysis() {
    @Throws(Exception::class)
    override fun init() {
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            // Define a function to plot
            val surface = getShape { delta, step ->
                delta to step
            }
            /*val surface1 = getShape(ColorMapGrayscale()) { delta, step ->
                delta to 100.toDouble()
            }*/

            // Create a chart
            val f: IChartFactory = AWTChartFactory()
            val chart = f.newChart(Quality.Nicest())
            chart.scene.graph.add(surface)
//            chart.scene.graph.add(surface1)
            chart.axisLayout.xAxisLabel = "Delta"
            chart.axisLayout.yAxisLabel = "Step"
            chart.setAxeDisplayed(true)
            chart.open()
            chart.addMouse()
            chart.addKeyboard()
        }

        /**
         * Gets the graph to draw on the chart
         * */
        private fun getShape(
            colorMap: IColorMap = ColorMapRainbow(),
            getDeltaAndStep: (Double, Double) -> Pair<Double, Double>
        ): Shape? {
            val func = Func3D { _delta: Double, _step: Double ->
                val (delta, step) = getDeltaAndStep(_delta, _step)
                var initialRate =
                    (delta * 100.0).roundToInt() / 100.0 //not exactly sure what the goal is. To remove the decimal?
                initialRate = if (delta > 0)
                    floor((initialRate + 0.005) / step) * step //not sure the significance of the number 0.005, happens to be tenth of speed step, but not sure if that is an actual relationship, or 0.005 is just half of 0.01. Not really sure how this math works, just copied from VLC
                else
                    ceil((initialRate - 0.005) / step) * step
                ((initialRate + delta) * 100.0).roundToInt() / 100.0
            }
            val range = Range(-100F, 100F)
            val steps = 80

            // Create the object to represent the function over the given range.
            val surface = SurfaceBuilder().orthonormal(OrthonormalGrid(range, steps), func)
            surface.colorMapper = ColorMapper(colorMap, surface, Color(1F, 1F, 1F, .5F))
            surface.isFaceDisplayed = true
            surface.isWireframeDisplayed = true
            surface.wireframeColor = Color.BLACK
            return surface
        }
    }
}