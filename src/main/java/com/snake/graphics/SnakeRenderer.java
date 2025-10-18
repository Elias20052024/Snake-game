package com.snake.graphics;

import java.awt.*;
import java.awt.geom.*;

public class SnakeRenderer {
    private static final Color SNAKE_GREEN = new Color(50, 205, 50); // Lighter, brighter green
    private static final Color SNAKE_DARK_GREEN = new Color(34, 139, 34);
    private static final Color EYE_WHITE = Color.WHITE;
    private static final Color EYE_BLACK = Color.BLACK;
    private static final Color TONGUE_COLOR = new Color(255, 105, 180); // Pink tongue

    public static void drawSnakeHead(Graphics2D g2d, int x, int y, int size, Direction direction) {
        // Enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Create main head shape (larger than body segments)
        int headSize = (int)(size * 1.3);
        int offset = (headSize - size) / 2;
        x -= offset;
        y -= offset;

        // Create gradient for 3D effect
        GradientPaint gradientPaint = new GradientPaint(
            x, y, SNAKE_GREEN,
            x + headSize, y + headSize, SNAKE_DARK_GREEN
        );
        g2d.setPaint(gradientPaint);

        // Draw main head shape as a circle
        Ellipse2D head = new Ellipse2D.Float(
            x, y, headSize, headSize
        );
        g2d.fill(head);
        
        // Draw outline
        g2d.setColor(SNAKE_DARK_GREEN);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(head);

        // Draw eyes based on direction
        drawEyes(g2d, x, y, headSize, direction);
        
        // Draw tongue
        drawTongue(g2d, x, y, headSize, direction);
    }
    
    private static void drawTongue(Graphics2D g2d, int x, int y, int headSize, Direction direction) {
        g2d.setColor(TONGUE_COLOR);
        int tongueLength = headSize / 3;
        int tongueWidth = headSize / 8;
        int startX = x + headSize/2;
        int startY = y + headSize/2;
        
        // Adjust start position based on direction
        switch(direction) {
            case RIGHT:
                startX = x + headSize - 2;
                break;
            case LEFT:
                startX = x + 2;
                tongueLength = -tongueLength;
                break;
            case DOWN:
                startY = y + headSize - 2;
                break;
            case UP:
                startY = y + 2;
                tongueLength = -tongueLength;
                break;
        }
        
        // Draw forked tongue
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            // Horizontal tongue
            g2d.setStroke(new BasicStroke(tongueWidth));
            g2d.drawLine(startX, startY, startX + tongueLength, startY);
            // Fork
            g2d.drawLine(startX + tongueLength, startY, startX + tongueLength + tongueWidth, startY - tongueWidth);
            g2d.drawLine(startX + tongueLength, startY, startX + tongueLength + tongueWidth, startY + tongueWidth);
        } else {
            // Vertical tongue
            g2d.setStroke(new BasicStroke(tongueWidth));
            g2d.drawLine(startX, startY, startX, startY + tongueLength);
            // Fork
            g2d.drawLine(startX, startY + tongueLength, startX - tongueWidth, startY + tongueLength + tongueWidth);
            g2d.drawLine(startX, startY + tongueLength, startX + tongueWidth, startY + tongueLength + tongueWidth);
        }
    }

    private static void drawEyes(Graphics2D g2d, int x, int y, int headSize, Direction direction) {
        int eyeSize = headSize / 4; // Larger eyes
        int eyeOffset = headSize / 4;
        
        // Eye positions based on direction
        Point[] eyePositions = getEyePositions(x, y, headSize, eyeOffset, direction);
        
        for (Point eyePos : eyePositions) {
            // Draw white of eye
            g2d.setColor(EYE_WHITE);
            Ellipse2D eye = new Ellipse2D.Float(
                eyePos.x, eyePos.y, eyeSize, eyeSize
            );
            g2d.fill(eye);
            
            // Add eye shine
            g2d.setColor(new Color(255, 255, 255, 180));
            int shineSize = eyeSize / 4;
            Ellipse2D shine = new Ellipse2D.Float(
                eyePos.x + shineSize/2, eyePos.y + shineSize/2,
                shineSize, shineSize
            );
            g2d.fill(shine);
            
            // Draw pupil (large and centered)
            g2d.setColor(EYE_BLACK);
            int pupilSize = (int)(eyeSize * 0.6);
            int pupilOffset = (eyeSize - pupilSize) / 2;
            Ellipse2D pupil = new Ellipse2D.Float(
                eyePos.x + pupilOffset, eyePos.y + pupilOffset,
                pupilSize, pupilSize
            );
            g2d.fill(pupil);
            
            // Draw eye outline
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.setColor(Color.BLACK);
            g2d.draw(eye);
        }
    }

    private static Point[] getEyePositions(int x, int y, int headSize, int eyeOffset, Direction direction) {
        Point[] positions = new Point[2];
        int eyeSpacing = headSize / 3;
        
        switch (direction) {
            case RIGHT:
                positions[0] = new Point(x + headSize - eyeOffset, y + eyeOffset);
                positions[1] = new Point(x + headSize - eyeOffset, y + headSize - 2*eyeOffset);
                break;
            case LEFT:
                positions[0] = new Point(x + eyeOffset, y + eyeOffset);
                positions[1] = new Point(x + eyeOffset, y + headSize - 2*eyeOffset);
                break;
            case UP:
                positions[0] = new Point(x + eyeOffset, y + eyeOffset);
                positions[1] = new Point(x + headSize - 2*eyeOffset, y + eyeOffset);
                break;
            case DOWN:
                positions[0] = new Point(x + eyeOffset, y + headSize - 2*eyeOffset);
                positions[1] = new Point(x + headSize - 2*eyeOffset, y + headSize - 2*eyeOffset);
                break;
        }
        return positions;
    }

    public static void drawSnakeBody(Graphics2D g2d, int x, int y, int size) {
        // Enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Create gradient for 3D effect
        GradientPaint gradientPaint = new GradientPaint(
            x, y, SNAKE_GREEN,
            x + size, y + size, SNAKE_DARK_GREEN
        );
        g2d.setPaint(gradientPaint);

        // Draw body segment as a circle
        int padding = 1;
        Ellipse2D body = new Ellipse2D.Float(
            x + padding, y + padding, 
            size - 2*padding, size - 2*padding
        );
        g2d.fill(body);
        
        // Draw outline
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(SNAKE_DARK_GREEN);
        g2d.draw(body);
    }
}