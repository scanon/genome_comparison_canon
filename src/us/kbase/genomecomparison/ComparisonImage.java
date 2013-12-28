package us.kbase.genomecomparison;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import us.kbase.common.service.Tuple3;

public class ComparisonImage {
	
	public static BufferedImage draw(ProteomeComparison cmp, double scalePercent) {
		int w = (int)(cmp.getProteome1names().size() * scalePercent / 100.0);
		int h = (int)(cmp.getProteome2names().size() * scalePercent / 100.0);
		BufferedImage ret = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = (Graphics2D)ret.getGraphics();
		gr.setColor(new Color(0, 75, 75));
		gr.fillRect(0, 0, w, h);
		for (int i = 0; i < cmp.getProteome1names().size(); i++) {
			int x = (int)(i * scalePercent / 100.0);
			List<Tuple3<Long, Long, Long>> hitList = cmp.getData1().get(i);
			for (Tuple3<Long, Long, Long> hit : hitList) {
				int y = h - 1 - (int)(hit.getE1() * scalePercent / 100.0);
				gr.setColor(getColor(hit.getE3()));
				gr.drawLine(x, y, x, y);
			}
		}
		gr.dispose();
		return ret;
	}
	
	private static Color getColor(long bbhPercent) {
		int gbPart = Math.min(255, Math.max(0, (int)(255.0 * (bbhPercent - 90.0) / 10.0)));
		return new Color(255, 255 - (255 - gbPart) / 2, gbPart);
	}
	
	public static void saveImage(ProteomeComparison cmp, double scalePercent, File ret) throws Exception {
		BufferedImage img = draw(cmp, scalePercent);
		ImageIO.write(img, "PNG", ret);
	}
}
