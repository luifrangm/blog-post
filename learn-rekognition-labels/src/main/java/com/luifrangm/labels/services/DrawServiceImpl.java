package com.luifrangm.labels.services;

import com.luifrangm.labels.models.LabelsGeometry;
import com.luifrangm.labels.utils.LabelUtils;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;

@Service
public class DrawServiceImpl implements DrawService {

    private static final BasicStroke BASIC_STROKE = new BasicStroke(3);

    @Override
    public File draw(File file, LabelsGeometry geometry) {
        File outputFile = new File(
            LabelUtils.renameFileName(file.getName()));
        return
            Try.of(() -> {
                BufferedImage image = ImageIO.read(file);
                Graphics2D graphics = image.createGraphics();
                graphics.setColor(new Color(94, 245, 33));
                graphics.setStroke(BASIC_STROKE);
                int x = BigDecimal.valueOf(image.getWidth() * geometry.getLeft()).intValue();
                int y = BigDecimal.valueOf(image.getHeight() * geometry.getTop()).intValue();
                int width = BigDecimal.valueOf(image.getWidth() * geometry.getWidth()).intValue();
                int height = BigDecimal.valueOf(image.getHeight() * geometry.getHeight()).intValue();
                graphics.drawRect(x, y, width, height);
                graphics.dispose();
                ImageIO.write(image, "jpg", outputFile);
                return outputFile;
            }).getOrNull();
    }
}
