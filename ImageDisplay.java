
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;


public class ImageDisplay {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne;
	int width = 512;
	int height = 512;

	/** Read Image RGB
	 *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
	 */
	private void readImageRGB(int width, int height, String imgPath, BufferedImage img, int h1, int h2)
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

			int ind = 0;
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2];

					if (compute_hsv(r,g,b,h1,h2)){
//						r = (byte) ((r+g+b)/3);
//						g = r;
//						b = r;
						r = (byte) Math.max((int)r, (int)g);
						b = (byte) Math.max((int)r, (int)b);
						r = b;
						g = b;
					}
					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x,y,pix);
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

	private boolean compute_hsv(byte r, byte g, byte b, int h1, int h2){
		int rr = r & 0xff;
		int gg = g & 0xff;
		int bb = b & 0xff;
		float red = (float) rr/255;
		float green = (float) gg/255;
		float blue = (float) bb/255;
		float max = Math.max(Math.max(red, blue), green);
		float min = Math.min(Math.min(red, blue), green);

		float h = 0;

		if (red == max) h = (green - blue) / (max - min);
		if (green == max) h = 2 + (blue - red) / (max - min);
		if (blue == max) h = 4 + (red - green) / (max - min);

		h *= 60;
		if (h<0) {
			h += 360;
		}

		// Threshold
		if (h <= h1) {
			return true;
		}
		else if (h >= h2) {
			return true;
		}
		return false;
	}


	public void showIms(String[] args){

		// Read a parameter from command line
		int h1 = Integer.parseInt(args[1]);
		int h2 = Integer.parseInt(args[2]);

		// Read in the specified image
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB(width, height, args[0], imgOne, h1, h2);

		// Use label to display the image
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		lbIm1 = new JLabel(new ImageIcon(imgOne));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		ImageDisplay ren = new ImageDisplay();
		ren.showIms(args);

		// ImagePath: "/Users/jinkunluo/Documents/CSCI576/Assignment2/roses_image1.rgb"
	}

}
