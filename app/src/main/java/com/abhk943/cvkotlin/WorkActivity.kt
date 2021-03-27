package com.abhk943.cvkotlin

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.util.*
import kotlin.collections.ArrayList


class WorkActivity : AppCompatActivity() {
    lateinit var _mainView:ImageView
    lateinit var _mainBitmap:Bitmap
    lateinit var _cannyView:ImageButton
    lateinit var _gammaView:ImageButton
    lateinit var _sobelView:ImageButton
    lateinit var _contourView:ImageButton
    lateinit var _bandwView:ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work)

        _mainView=findViewById(R.id.imageViewMain)
        _cannyView=findViewById(R.id.canny)
        _mainBitmap=setBitmap(_mainView)
        _gammaView=findViewById(R.id.gammaPower)
        _sobelView=findViewById(R.id.sobel)
        _contourView=findViewById(R.id.contours)
        _bandwView=findViewById(R.id.bandw)
        
        _cannyView.setOnClickListener{
            val resultBitmap = getCanny(_mainBitmap)

            _cannyView.setImageBitmap(resultBitmap)
            _mainView.setImageBitmap(resultBitmap)
        }
        _gammaView.setOnClickListener{
            val resultBitmap = getGamma(_mainBitmap)

            _gammaView.setImageBitmap(resultBitmap)
            _mainView.setImageBitmap(resultBitmap)
        }
        _sobelView.setOnClickListener{
            val _sobelMap = getSobel(_mainBitmap)
            _sobelView.setImageBitmap(_sobelMap)
            _mainView.setImageBitmap(_sobelMap)
        }
        _contourView.setOnClickListener{
            val resultBitmap = getCountour(_mainBitmap)
            _contourView.setImageBitmap(resultBitmap)
            _mainView.setImageBitmap(resultBitmap)
        }
        _bandwView.setOnClickListener{
            val resultBitmap = getBandW(_mainBitmap)
            _bandwView.setImageBitmap(resultBitmap)
            _mainView.setImageBitmap(resultBitmap)
        }
    }

    private fun getBandW(compressImage: Bitmap): Bitmap {
        var imageMat=bitmapMat(compressImage)
        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2GRAY)
        Imgproc.GaussianBlur(imageMat, imageMat, Size(3.0 , 3.0), 0.0)
        //Imgproc.adaptiveThreshold(imageMat, imageMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5, 4);
        //Imgproc.medianBlur(imageMat, imageMat, 3);
        Imgproc.threshold(imageMat, imageMat, 0.0, 255.0, Imgproc.THRESH_OTSU)

        val resultBitmap = Bitmap.createBitmap(imageMat.cols(),imageMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(imageMat, resultBitmap)
        return resultBitmap
    }

    private fun getCountour(originalBitmap: Bitmap): Bitmap {
        var originalMat=bitmapMat(originalBitmap)
        val grayMat = Mat()
        val cannyEdges = Mat()
        val hierarchy = Mat()

        val contourList: List<MatOfPoint> = ArrayList() //A list to store all the contours

        //Converting the image to grayscale
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY)

        Imgproc.Canny(originalMat, cannyEdges, 10.0, 100.0)

        //finding contours
        Imgproc.findContours(cannyEdges, contourList, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)

        //Drawing contours on a new image
        val contours = Mat()
        contours.create(cannyEdges.rows(), cannyEdges.cols(), CvType.CV_8UC3)
        val r = Random()
        for (i in contourList.indices) {
            Imgproc.drawContours(contours, contourList, i, Scalar(r.nextInt(255).toDouble(), r.nextInt(255).toDouble(), r.nextInt(255).toDouble()), -1)
        }
        //Converting Mat back to Bitmap
        val resultBitmap = Bitmap.createBitmap(contours.cols(), contours.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(contours, resultBitmap)
        return resultBitmap

    }

    private fun getGamma(originalBitmap: Bitmap): Bitmap {
        var param:Double = 0.5
        val srcMat = bitmapMat(originalBitmap)
        val gray = Mat(srcMat.size(), CvType.CV_8UC1)
        Imgproc.cvtColor(srcMat, gray, Imgproc.COLOR_RGB2GRAY)
        val dst:Mat=gray.clone()
//        Log.d("Channels", gray.channels().toString())
//        Log.d("Size", gray.get(0,0).size.toString())
        for (i in 0..gray.rows()-1){
            for (j in 0..gray.cols()-1){
//                Log.d("Values", gray.get(i,j)[0].toString())
                var valu=255*Math.pow(gray.get(i, j)[0] / 255, param)
                dst.put(i, j, valu)
            }

        }
        val resultBitmap = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(dst, resultBitmap)
        return resultBitmap
    }

    private fun getCanny(originalBitmap: Bitmap):Bitmap{
        val srcMat = bitmapMat(originalBitmap)
        val gray = Mat(srcMat.size(), CvType.CV_8UC1)
        Imgproc.cvtColor(srcMat, gray, Imgproc.COLOR_RGB2GRAY)
        val edge = Mat()
        val dst = Mat()
        Imgproc.Canny(gray, edge, 80.0, 90.0)
        Imgproc.cvtColor(edge, dst, Imgproc.COLOR_GRAY2RGBA, 4)
        val resultBitmap = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(dst, resultBitmap)
        return resultBitmap
    }
    private fun getSobel(originalBitmap: Bitmap): Bitmap {
        val originalMat=bitmapMat(originalBitmap)
        val grayMat = Mat()
        val sobel = Mat() //Mat to store the final result

        //Matrices to store gradient and absolute gradient respectively
        val grad_x = Mat()
        val abs_grad_x = Mat()
        val grad_y = Mat()
        val abs_grad_y = Mat()

        //Converting the image to grayscale
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY)

        //Calculating gradient in horizontal direction
        Imgproc.Sobel(grayMat, grad_x, CvType.CV_16S, 1, 0, 3, 1.0, 0.0)

        //Calculating gradient in vertical direction
        Imgproc.Sobel(grayMat, grad_y, CvType.CV_16S, 0, 1, 3, 1.0, 0.0)

        //Calculating absolute value of gradients in both the direction
        Core.convertScaleAbs(grad_x, abs_grad_x)
        Core.convertScaleAbs(grad_y, abs_grad_y)

        //Calculating the resultant gradient
        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 1.0, sobel)

        //Converting Mat back to Bitmap
        val resultBitmap = Bitmap.createBitmap(sobel.cols(), sobel.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(sobel, resultBitmap)
        return  resultBitmap
    }

    private fun setBitmap(_mainView: ImageView?): Bitmap {
        val returnMap = (_mainView!!.drawable as BitmapDrawable).bitmap
        return returnMap
    }
    private fun bitmapMat(_bitmap: Bitmap): Mat{
        val srcMat = Mat(_bitmap.getHeight(), _bitmap.getWidth(), CvType.CV_8UC3)

        // Copying original
        val myBitmap32: Bitmap = _bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Utils.bitmapToMat(myBitmap32, srcMat)
        return  srcMat
    }
}