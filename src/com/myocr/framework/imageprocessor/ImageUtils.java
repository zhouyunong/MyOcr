package com.myocr.framework.imageprocessor;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

/**
 * 图像处理工具
 * @author elvira
 */
public class ImageUtils {
	private static final String TAG = "ImageUtils";

	public static final int BLACK     = -16777216; // 黑色
	public static final int WHITE     = -1;        // 白色
	public static final int EXPANDNUM = 18;        // 膨胀系数

	/**
	 * 将图像灰度化 
	 * author elvira
	 * @param Bitmap
	 * @return Bitmap
	 */
	public static Bitmap bitmap2Gray(Bitmap bmSrc) {
		int width  = bmSrc.getWidth();
		int height = bmSrc.getHeight();
		// 创建目标灰度图像
		Bitmap bmpGray = null;
		bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		// 创建画布
		Canvas c = new Canvas(bmpGray);
		Paint paint = new Paint();
	
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmSrc, 0, 0, paint);
		return bmpGray;
	}

	/**
	 * 图像线性灰度化,暂时没用 
	 * author elvira
	 * @param Bitmap
	 * @return Bitmap
	 */
	public static Bitmap lineGrey(Bitmap image) {
		int width  = image.getWidth();
		int height = image.getHeight();
		// 创建线性拉升灰度图像
		Bitmap linegray = image;
		// 依次循环对图像的像素进行处理
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// 得到每点的像素值
				int col   = image.getPixel(i, j);
				int alpha = col & 0xFF000000;
				int red   = (col & 0x00FF0000) >> 16;
				int green = (col & 0x0000FF00) >> 8;
				int blue  = (col & 0x000000FF);
				// 增加了图像的亮度
				red   = (int) (1.1 * red + 30);
				green = (int) (1.1 * green + 30);
				blue  = (int) (1.1 * blue + 30);
				// 对图像像素越界进行处理
				if (red >= 255) {
					red = 255;
				}

				if (green >= 255) {
					green = 255;
				}

				if (blue >= 255) {
					blue = 255;
				}
				// 新的ARGB
				int newColor = alpha | (red << 16) | (green << 8) | blue;
				// 设置新图像的RGB值
				linegray.setPixel(i, j, newColor);
			}
		}
		return linegray;
	}

	/**
	 * 图像二值化
	 * author  elvira
	 * @param  Bitmap
	 *         灰度化的Bitmap
	 * @return Bitmap
	 */
	public static Bitmap gray2Binary(Bitmap graymap) {
		// 得到图形的宽度和长度
		int width = graymap.getWidth();
		int height = graymap.getHeight();
		// 创建二值化图像
		Bitmap binarymap = graymap;
		// 依次循环，对图像的像素进行处理
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// 得到当前像素的值
				int col   = binarymap.getPixel(i, j);
				int alpha = col & 0xFF000000;
				int red   = (col & 0x00FF0000) >> 16;
				int green = (col & 0x0000FF00) >> 8;
				int blue  = (col & 0x000000FF);
				// 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
				int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
				// 对图像进行二值化处理
				if (gray <= 95) {
					gray = 0;
				} else {
					gray = 255;
				}
				// 新的ARGB
				int newColor = alpha | (gray << 16) | (gray << 8) | gray;
				// 设置新图像的当前像素值
				binarymap.setPixel(i, j, newColor);
			}
		}
		return binarymap;
	}

	/*
	 * Robert算子梯度
	 */
	public static Bitmap RobertGradient(Bitmap myBitmap) {
		myBitmap = myBitmap.copy(Config.ARGB_8888, true);
		int width  = myBitmap.getWidth();
		int height = myBitmap.getHeight();
		// 创建二值化图像
		Bitmap robertmap = myBitmap;
		// 依次循环，对图像的像素进行处理
		for (int i = 0; i < width - 1; i++) {
			for (int j = 0; j < height - 1; j++) {
				// 得到当前像素的值
				int col00 = robertmap.getPixel(i, j);
				int col01 = robertmap.getPixel(i, j + 1);
				int col10 = robertmap.getPixel(i + 1, j);
				int col11 = robertmap.getPixel(i + 1, j + 1);
				// 新的ARGB
				int newColor = Math.abs(col00 - col11)
						+ Math.abs(col10 - col01);
				// 设置新图像的当前像素值
				robertmap.setPixel(i, j, newColor);
			}
		}
		Log.i(TAG, "边缘检测成功");
		return robertmap;
	}

	/*
	 * HACK:Sobel算子锐化，未测试，不一定正确
	 */
	public static Bitmap SobelGradient(Bitmap myBitmap) {
		int width  = myBitmap.getWidth();
		int height = myBitmap.getHeight();
		// 创建二值化图像
		Bitmap sobelmap = myBitmap;
		// 依次循环，对图像的像素进行处理
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				// 得到当前像素的值
				int col00 = sobelmap.getPixel(i - 1, j - 1);
				int col01 = sobelmap.getPixel(i - 1, j);
				int col02 = sobelmap.getPixel(i - 1, j + 1);
				int col10 = sobelmap.getPixel(i, j - 1);
				int col12 = sobelmap.getPixel(i, j + 1);
				int col20 = sobelmap.getPixel(i + 1, j - 1);
				int col21 = sobelmap.getPixel(i + 1, j);
				int col22 = sobelmap.getPixel(i + 1, j + 1);
				// 新的ARGB
				int newColor = Math.abs((col20 + 2 * col21 + col22)
						- (col00 + 2 * col01 + col00))
						+ Math.abs((col02 + 2 * col12 + col22)
								- (col00 + 2 * col10 + col20));
				// 设置新图像的当前像素值
				sobelmap.setPixel(i, j, newColor);
			}
		}
		return sobelmap;
	}

	/*
	 * HACK:Laplace 锐化，未测试，不一定正确
	 */
	public static Bitmap LaplaceGradient(Bitmap myBitmap) {
		int width  = myBitmap.getWidth();
		int height = myBitmap.getHeight();
		// 创建二值化图像
		Bitmap laplacemap = myBitmap;
		// 依次循环，对图像的像素进行处理
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				// 得到当前像素的值
				int col01 = laplacemap.getPixel(i - 1, j);
				int col10 = laplacemap.getPixel(i, j - 1);
				int col11 = laplacemap.getPixel(i, j);
				int col12 = laplacemap.getPixel(i, j + 1);
				int col21 = laplacemap.getPixel(i + 1, j);
				// 新的ARGB
				int newColor = Math.abs(5 * col11 - col21 - col01 - col12
						- col10);
				// 设置新图像的当前像素值
				laplacemap.setPixel(i, j, newColor);
			}
		}
		return laplacemap;
	}

	/**
	 * 倾斜校正
	 * author elvira
	 * @param binaryBitmap
	 *        二值化的Bitmap
	 * @return
	 */
	/*public static Bitmap TiltCorrection(Bitmap binaryBitmap) {
		int width  = binaryBitmap.getWidth();
		int height = binaryBitmap.getHeight();
		//膨胀
		Bitmap output = expand(binaryBitmap);
		//边缘检测
		output = RobertGradient(output);
		//计算角度
		int ratate = calRatate(output);
		//旋转
		Matrix matrix = new Matrix();
		matrix.setRotate(ratate);
		output = Bitmap.createBitmap(binaryBitmap, 0, 0, width, height, matrix,
				true);
		Log.i(TAG, "旋转成功");
		return output;
	}*/

	
	public static Bitmap expand(Bitmap binaryBitmap) {
		int width  = binaryBitmap.getWidth();
		int height = binaryBitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(binaryBitmap);
		int nSize = EXPANDNUM / 2;
		//膨胀
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int pix = binaryBitmap.getPixel(i, j); // 从二值化数据取出数据
				if (pix == BLACK) {
					for (int k = i - nSize; k <= i + nSize; k++) {
						for (int m = j - nSize; m <= j + nSize; m++) {
							if (k < 0 || k >= width || m < 0 || m >= height)
								continue;
							if (output.getPixel(k, m) == BLACK)
								continue;
							output.setPixel(k, m, BLACK);
						}
					}
				}
			}
		}
		Log.i(TAG, "膨胀成功");
		return output;
	}

	/**
	 * 计算角度
	 * author elvira
	 * @param myBitmap
	 * @return
	 */
	private static int calRatate(Bitmap myBitmap) {
		int width = myBitmap.getWidth();
		int height = myBitmap.getHeight();
		//最长距离
		int iRMax = (int) Math.sqrt(width * width + height * height) + 1;
		//最大角度
		int iThMax = 181;
		int iMax = -1;           //记录图像最大值
		int iThMaxIndex = -1;    //记录图像最大角度
		float fRate = (float) (3.14159 / 180);
		char[] pArray = new char[iRMax * iThMax];   //累积数组

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (myBitmap.getPixel(x, y) == WHITE) {
					for (int iTh = 1; iTh < iThMax; iTh++) {
						//转换为极坐标下计算距离
						int iR = (int) (y * Math.acos(iTh * fRate) + x
								* Math.asin(iTh * fRate));
						if (iR > 0 && iR < iRMax) {
							pArray[iR * iThMax + iTh]++;
						}
					}
				}
			}
		}
		//遍历寻找累积最大的点
		for (int iR = 1; iR < iRMax; iR++) {
			for (int iTh = 1; iTh < iThMax; iTh++) {
				int iCount = pArray[iR * iThMax + iTh];
				if (iCount > iMax) {
					iMax = iCount;
					iThMaxIndex = iTh;
				}
			}
		}
		Log.i(TAG, "倾斜度测量成功,角度：" + iThMaxIndex);
		return iThMaxIndex;
	}

	
	public static Bitmap compression(Bitmap myBitmap) {
		int width  = myBitmap.getWidth();
		int height = myBitmap.getHeight();
		int newheight = (int)Math.ceil(height/2);
		int newwidth = (int)Math.ceil(width/2);
		int[] colors = new int[newheight*newwidth];
		int count = 0;
		Log.e(TAG, "width："+ width + " newwidth:"+newwidth);
		for (int m = 0; m < height; m+=2) {
			for (int n = 0; n < width; n+=2) {
				int totle = myBitmap.getPixel(n, m);
				
				if (m == width-1 && n == height-1){
					totle+=3*WHITE;
				}else if (m == height-1){
					totle+=myBitmap.getPixel(n+1, m)+2*WHITE;
				}else if (n == width-1){
					totle+=myBitmap.getPixel(n, m+1)+2*WHITE;
				}else totle+=myBitmap.getPixel(n, m+1)+myBitmap.getPixel(n+1, m)+myBitmap.getPixel(n+1, m+1);
				if (count>=newheight*newwidth)
					break;
				if (totle == WHITE*4) //4格全为0
					colors[count++] = WHITE;
				else colors[count++] = BLACK;	
			}
		}
		Bitmap output = Bitmap.createBitmap(colors, newwidth, newheight, Config.RGB_565);
		return output;
	}
	/**
	 * 倾斜校正
	 * author elvira
	 * @param binaryBitmap
	 *        二值化的Bitmap
	 * @return
	 */
	public static Bitmap TiltCorrection(Bitmap binaryBitmap) {
		
		long timestart = System.currentTimeMillis();
       int width  = binaryBitmap.getWidth();
       int height = binaryBitmap.getHeight();
       //压缩
       Bitmap output = compression(binaryBitmap);
		//膨胀
       output = expand(output);
//       output = output.copy(Config.ARGB_8888, false);
		//边缘检测
       output = RobertGradient(output);
//		//计算角度
//		int ratate = calRatate(output);
//		//旋转
//		Matrix matrix = new Matrix();
//		matrix.setRotate(ratate);
//		output = Bitmap.createBitmap(binaryBitmap, 0, 0, width, height, matrix,
//				true);
//		Log.i(TAG, "旋转成功");
		Log.i(TAG, "倾斜校正共消耗："+ (System.currentTimeMillis()-timestart));
		return output;
	}
}
