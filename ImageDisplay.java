
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;


public class ImageDisplay {

	JFrame frame;
	JLabel lbIm1;
	JPanel jpanel;
	BufferedImage imgOne;
	int width = 512;
	int height = 512;

	float scale;
	float degree;
	boolean aliasing;
	float fps;
	int curr_time = 1;
	float time;
	double radian;
	float total_frame;
	float time_per_round;

	byte[] img_rgb;

	float new_width;
	float new_height;

	/**
	 * Check invalid value of input
	 * For degree: Convert value into 0-360
	 * @param args
	 */
	private void invalid_value_check(String[] args) {

		String path_img = args[0];
		String save_path; // path to save image
		String save_name = "NewImage";
		String save_suffix = "jpg"; // TODO: the type of new image
		scale = Float.parseFloat(args[1]);
		degree = Float.parseFloat(args[2]);
		aliasing = Boolean.parseBoolean(args[3]);
		fps = Float.parseFloat(args[4]);
		time = Float.parseFloat(args[5]);
		total_frame = fps * time;

		if (scale <= 0) {
			try {
				throw new Exception("Scale value must greater than 0!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (fps < 0) {
			try {
				throw new Exception("Please enter a valid Number of Frames per second(EQUAL or GREATER than 0)!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (time < 0) {
			try {
				throw new Exception("Please enter a valid value of Transition Time(EQUAL or GREATER than 0)!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Compute the value of
	 * @param x_axis
	 * @param y_axis
	 * @return new x and y
	 */
	private int[] compute_x_y(int x_axis, int y_axis){

		int[] new_axises = new int[2];

		float new_scale = scale;
		float new_degree = degree;

		if (scale>0) {
			new_width = (float) (Math.sqrt(2.0) * width) * scale;
			new_height = (float) (Math.sqrt(2.0) * height) * scale;
		}
		else {
			new_width = (float) (Math.sqrt(2.0) * width);
			new_height = (float) (Math.sqrt(2.0) * height);
		}


		if (fps > 0) {
			// Compute degree of each frame
			total_frame = fps*time;
			time_per_round = (float)1/fps;
			// Compute scale percent
			// scale > 1: (scale - 1)/total_frame + 1
			// scale < 1: 1 - (1 - scale)/total_frame

			// time of recursion
			new_degree = degree / total_frame * curr_time;

			if (scale >= 1) {
				float delta_scale = (scale-1) / total_frame;
				new_scale = delta_scale*curr_time + 1;
			}
			else {
				float delta_scale = (1- scale) / total_frame;
				new_scale = 1 - delta_scale*curr_time;
			}
		}

		new_degree %= 360;
		if (new_degree < 0) {
			new_degree += 360;
		}
		radian = Math.toRadians(new_degree);

		int[] origin_axises = new int[2];
		origin_axises[0] = x_axis-width/2;
		origin_axises[1] = y_axis-height/2;

		int transformed_x = (int)(origin_axises[0]*Math.cos(radian)*new_scale - origin_axises[1]*Math.sin(radian)*new_scale);
		int transformed_y = (int)(origin_axises[0]*Math.sin(radian)*new_scale + origin_axises[1]*Math.cos(radian)*new_scale);

		new_axises[0] = transformed_x+(int)width/2;
		new_axises[1] = transformed_y+(int)height/2;

		return new_axises;
	}

	private void readImage(int width, int height, String imgPath) {
		try
		{
			int frameLength = width*height*3;

			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			long len = frameLength;
//			byte[] bytes = new byte[(int) len];
//
//			raf.read(bytes);
			img_rgb = new byte[(int) len];
			raf.read(img_rgb);
		}

		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	/** Read Image RGB
	 *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
	 */
	private void readImageRGB(int img_width, int img_height, String imgPath, BufferedImage img)
	{
		try
		{
			int frameLength = width*height*3;

			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];

			raf.read(bytes);

			if (aliasing) {
				int ind = 0;
				int r_ = 0;
				int g_ = 0;
				int b_ = 0;
				int pix = 0;
				for (int y = 0; y < height; y += 3) {
					for (int x = 0; x < width; x += 3) {
						if ((y % 3 == 0) && (x % 3 == 0)) {
							r_ = (bytes[ind] + bytes[ind + 1] + bytes[ind + 2] + bytes[ind + width] + bytes[ind + 1 + width] + bytes[ind + 2 + width] + bytes[ind + width * 2] + bytes[ind + 1 + width * 2] + bytes[ind + 2 + width * 2]) / 9;
							g_ = (bytes[ind + height * width] + bytes[ind + 1 + height * width] + bytes[ind + 2 + height * width] + bytes[ind + width + height * width] + bytes[ind + 1 + width + height * width] + bytes[ind + 2 + width + height * width] + bytes[ind + width * 2 + height * width] + bytes[ind + 1 + width * 2 + height * width] + bytes[ind + 2 + width * 2 + height * width]) / 9;
							b_ = (bytes[ind + height * width * 2] + bytes[ind + 1 + height * width * 2] + bytes[ind + 2 + height * width * 2] + bytes[ind + width + height * width * 2] + bytes[ind + 1 + width + height * width * 2] + bytes[ind + 2 + width + height * width * 2] + bytes[ind + width * 2 + height * width * 2] + bytes[ind + 1 + width * 2 + height * width * 2] + bytes[ind + 2 + width * 2 + height * width * 2]) / 9;
							pix = 0xff000000 | (((byte) r_ & 0xff) << 16) | (((byte) g_ & 0xff) << 8) | ((byte) b_ & 0xff);
						}
						for (int i = 0; i < 9; i++) {
							img.setRGB((x + i / 3), (y + i % 3), pix);
						}
						ind += 3;
					}
				}
			}
			int ind = 0;
			for (int x = 0; x < img_width; x++){
				for (int y=0; y< img_height; y++){
					img.setRGB(x, y, 0);
				}
			}
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind + height * width];
					byte b = bytes[ind + height * width * 2];

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
//					img.setRGB(x,y,pix);
					// TODO: change x and y here --> For try
//					img.setRGB((int)(x*0.8), (int)(y*0.8), pix);
					int new_X = compute_x_y(x, y)[0];
					int new_Y = compute_x_y(x, y)[1];
					if (new_X >=0 && new_X < img_width && new_Y>=0 && new_Y<img_height) {
						img.setRGB(new_X, new_Y, pix);
					}

//					System.out.println(curr_time);
					ind++;
				}
			}
		}

		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * showImage from readImageRGB function
	 * @param args --> args[0]:imgPath used in readImageRGB function
	 */

	public void showIms(String args) {

		// Read a parameter from command line

		// Read in the specified image
		imgOne = new BufferedImage((int) ((int)width*scale), (int) ((int)height*scale), BufferedImage.TYPE_INT_RGB);

		// Use label to display the image
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);
		jpanel = new JPanel();
//		lbIm1 = new JLabel(new ImageIcon(imgOne));


		if (fps > 0) {
			for (int i = 0; i < total_frame; i++) {
				readImageRGB((int)(width*scale), (int)(height*scale), args, imgOne);
				ImageIcon img = new ImageIcon(imgOne);
//				Graphics2D g2d = imgOne.createGraphics();
//				Image tmp = img.getImage();
//				g2d.drawImage(tmp, 0, 0, null);
				displayFrame(img);
				curr_time ++;
//				g2d.dispose();

				try {
					Thread.sleep((long) ((1000.0 / fps)));
					frame.remove(lbIm1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			readImageRGB((int)(width*scale), (int)(height*scale), args, imgOne);
			ImageIcon img = new ImageIcon(imgOne);
			displayFrame(img);
		}
	}

	private void displayFrame(ImageIcon image) {
		lbIm1 = new JLabel(image);
		frame.getContentPane().add(lbIm1);
		frame.pack();
		frame.setVisible(true);

	}

	// TODO: how to deal with .exe

	/**
	 *
	 * @param input -- 6 parameters
	 *             "path" - 0
	 *             scale - 1
	 *             degree - 2
	 *             aliasing - 3
	 *             fps - 4
	 *             time - 5
	 */

	public static void main(String[] args) {
//		args = new String[] {"/Users/jinkunluo/Documents/CSCI576/CSCI576-HW1/lake-forest_512_512.rgb", "1.2", "50", "0", "25", "4"};
		ImageDisplay ren = new ImageDisplay();
		ren.invalid_value_check(args);
		ren.showIms(args[0]);
	}

}
