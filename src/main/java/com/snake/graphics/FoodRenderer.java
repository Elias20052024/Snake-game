package com.snake.graphics;

import java.awt.*;
import java.awt.geom.*;

public class FoodRenderer {
    private static final Color[] FRUIT_COLORS = {
        new Color(255, 0, 0),     // Apple
        new Color(255, 165, 0),   // Orange
        new Color(255, 255, 0),   // Lemon
        new Color(128, 0, 128),   // Grape
        new Color(255, 192, 203)  // Strawberry
    };

    private static final Color[] FRUIT_SHADOW_COLORS = {
        new Color(139, 0, 0),     // Dark Red
        new Color(205, 133, 0),   // Dark Orange
        new Color(205, 205, 0),   // Dark Yellow
        new Color(85, 0, 85),     // Dark Purple
        new Color(205, 140, 149)  // Dark Pink
    };

    private final int currentFruitIndex;

    public FoodRenderer() {
        this.currentFruitIndex = (int)(Math.random() * FRUIT_COLORS.length);
    }

    public void drawFood(Graphics2D g2d, int x, int y, int size) {
        Color mainColor = FRUIT_COLORS[currentFruitIndex];
        Color shadowColor = FRUIT_SHADOW_COLORS[currentFruitIndex];

        // Draw main fruit body
        drawFruitBody(g2d, x, y, size, mainColor, shadowColor);

        // Draw fruit-specific details
        switch (currentFruitIndex) {
            case 0: // Apple
                drawApple(g2d, x, y, size);
                break;
            case 1: // Orange
                drawOrange(g2d, x, y, size);
                break;
            case 2: // Lemon
                drawLemon(g2d, x, y, size);
                break;
            case 3: // Grape
                drawGrape(g2d, x, y, size);
                break;
            case 4: // Strawberry
                drawStrawberry(g2d, x, y, size);
                break;
        }
    }

    private void drawFruitBody(Graphics2D g2d, int x, int y, int size, Color mainColor, Color shadowColor) {
        // Save the original transform
        AffineTransform originalTransform = g2d.getTransform();
        
        // Apply slight rotation for more natural look
        g2d.rotate(Math.toRadians(Math.random() * 360), x + size/2, y + size/2);
        
        // Create radial gradient for more realistic 3D effect
        Point2D center = new Point2D.Float(x + size/3, y + size/3);
        float radius = size;
        float[] dist = {0.0f, 0.7f, 1.0f};
        Color[] colors = {
            new Color(
                Math.min(255, mainColor.getRed() + 50),
                Math.min(255, mainColor.getGreen() + 50),
                Math.min(255, mainColor.getBlue() + 50)
            ),
            mainColor,
            shadowColor
        };
        RadialGradientPaint gradientPaint = new RadialGradientPaint(
            center, radius, dist, colors
        );
        g2d.setPaint(gradientPaint);

        // Draw main fruit shape
        Ellipse2D fruit = new Ellipse2D.Float(x, y, size, size);
        g2d.fill(fruit);

        // Add glossy highlight for 3D effect
        int highlightSize = size / 3;
        GradientPaint highlightPaint = new GradientPaint(
            x + size/6, y + size/6, new Color(255, 255, 255, 160),
            x + size/2, y + size/2, new Color(255, 255, 255, 0)
        );
        g2d.setPaint(highlightPaint);
        Ellipse2D highlight = new Ellipse2D.Float(
            x + size/6, y + size/6, highlightSize, highlightSize
        );
        g2d.fill(highlight);
        
        // Add shadow at the bottom
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillOval(x + size/8, y + size*2/3, size*3/4, size/4);
        
        // Restore the original transform
        g2d.setTransform(originalTransform);
    }

    private void drawApple(Graphics2D g2d, int x, int y, int size) {
        // Draw curved stem
        g2d.setColor(new Color(101, 67, 33));
        QuadCurve2D stem = new QuadCurve2D.Float(
            x + size/2, y - size/6,
            x + size/2 + size/8, y - size/3,
            x + size/2 + size/6, y - size/4
        );
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(stem);
        
        // Draw detailed leaf
        g2d.setColor(new Color(0, 150, 0));
        Path2D leaf = new Path2D.Float();
        leaf.moveTo(x + size/2, y - size/6);
        leaf.curveTo(
            x + size/2 + size/4, y - size/4,
            x + size/2 + size/3, y - size/3,
            x + size/2 + size/6, y - size/2
        );
        leaf.curveTo(
            x + size/2 + size/8, y - size/3,
            x + size/2 + size/8, y - size/4,
            x + size/2, y - size/6
        );
        g2d.fill(leaf);
        
        // Add leaf vein
        g2d.setColor(new Color(0, 100, 0));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(
            x + size/2, y - size/6,
            x + size/2 + size/6, y - size/3
        );
    }

    private void drawOrange(Graphics2D g2d, int x, int y, int size) {
        // Draw detailed texture lines for segments
        g2d.setColor(new Color(205, 133, 0, 120));
        g2d.setStroke(new BasicStroke(1.5f));
        
        // Draw segment lines
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(360.0 / 8 * i);
            int startX = x + size/2;
            int startY = y + size/2;
            int endX = x + size/2 + (int)(Math.cos(angle) * size/2);
            int endY = y + size/2 + (int)(Math.sin(angle) * size/2);
            g2d.drawLine(startX, startY, endX, endY);
        }
        
        // Draw circular texture
        g2d.setStroke(new BasicStroke(0.8f));
        for (int i = 1; i <= 3; i++) {
            int diameter = size * i / 4;
            g2d.drawOval(
                x + (size - diameter)/2,
                y + (size - diameter)/2,
                diameter, diameter
            );
        }
    }

    private void drawLemon(Graphics2D g2d, int x, int y, int size) {
        // Draw detailed texture pattern
        g2d.setColor(new Color(218, 165, 32, 100));
        g2d.setStroke(new BasicStroke(0.8f));
        
        // Draw spiral pattern
        double angle;
        double spiralRadius;
        int centerX = x + size/2;
        int centerY = y + size/2;
        
        for (double t = 0; t < 10 * Math.PI; t += 0.2) {
            angle = t;
            spiralRadius = size/6 * (1 + t/(10*Math.PI));
            
            int x1 = (int)(centerX + spiralRadius * Math.cos(angle));
            int y1 = (int)(centerY + spiralRadius * Math.sin(angle));
            int x2 = (int)(centerX + spiralRadius * Math.cos(angle + 0.2));
            int y2 = (int)(centerY + spiralRadius * Math.sin(angle + 0.2));
            
            if (x1 >= x && x1 <= x + size && y1 >= y && y1 <= y + size &&
                x2 >= x && x2 <= x + size && y2 >= y && y2 <= y + size) {
                g2d.drawLine(x1, y1, x2, y2);
            }
        }
        
        // Add bumpy texture
        g2d.setColor(new Color(218, 165, 32, 50));
        for (int i = 0; i < 20; i++) {
            int bumpX = x + size/4 + (int)(Math.random() * size/2);
            int bumpY = y + size/4 + (int)(Math.random() * size/2);
            g2d.fillOval(bumpX, bumpY, 3, 3);
        }
    }

    private void drawGrape(Graphics2D g2d, int x, int y, int size) {
        // Create a frosted effect
        g2d.setColor(new Color(255, 255, 255, 30));
        
        // Draw multiple highlight spots for frosted look
        for (int i = 0; i < 3; i++) {
            int highlightSize = size/6;
            int posX = x + size/4 + (int)(Math.random() * size/2);
            int posY = y + size/4 + (int)(Math.random() * size/2);
            
            RadialGradientPaint frost = new RadialGradientPaint(
                new Point2D.Float(posX + highlightSize/2, posY + highlightSize/2),
                highlightSize,
                new float[]{0.0f, 1.0f},
                new Color[]{
                    new Color(255, 255, 255, 150),
                    new Color(255, 255, 255, 0)
                }
            );
            
            g2d.setPaint(frost);
            g2d.fillOval(posX, posY, highlightSize, highlightSize);
        }
        
        // Add a subtle blue tint overlay
        g2d.setColor(new Color(100, 100, 255, 20));
        g2d.fillOval(x + size/8, y + size/8, size*3/4, size*3/4);
    }

    private void drawStrawberry(Graphics2D g2d, int x, int y, int size) {
        // Draw more realistic seeds pattern
        g2d.setColor(new Color(255, 255, 150));
        int rows = 6;
        int cols = 6;
        double angleOffset = Math.random() * Math.PI;
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Calculate seed position in a more natural pattern
                double angle = (2 * Math.PI * j / cols) + (i % 2 * Math.PI / cols) + angleOffset;
                double radius = size * (0.2 + 0.3 * i / rows);
                int seedX = x + size/2 + (int)(radius * Math.cos(angle));
                int seedY = y + size/2 + (int)(radius * Math.sin(angle));
                
                // Draw elongated seed
                AffineTransform originalTransform = g2d.getTransform();
                g2d.translate(seedX, seedY);
                g2d.rotate(angle + Math.PI/4);
                g2d.fillOval(-2, -1, 4, 2);
                g2d.setTransform(originalTransform);
            }
        }
    }
}