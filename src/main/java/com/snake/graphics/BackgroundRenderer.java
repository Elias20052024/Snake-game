package com.snake.graphics;

import java.awt.*;

public class BackgroundRenderer {
    private static final Color[][] LEVEL_COLORS = {
        {new Color(50, 150, 50), new Color(0, 100, 0)},      // Level 1: Green gradients
        {new Color(70, 70, 150), new Color(20, 20, 100)},    // Level 2: Blue gradients
        {new Color(150, 70, 70), new Color(100, 20, 20)},    // Level 3: Red gradients
        {new Color(150, 150, 70), new Color(100, 100, 20)},  // Level 4: Yellow gradients
        {new Color(150, 70, 150), new Color(100, 20, 100)},  // Level 5: Purple gradients
        {new Color(70, 150, 150), new Color(20, 100, 100)},  // Level 6: Cyan gradients
        {new Color(150, 100, 50), new Color(100, 50, 0)},    // Level 7: Orange gradients
        {new Color(100, 50, 150), new Color(50, 0, 100)},    // Level 8: Indigo gradients
        {new Color(150, 50, 100), new Color(100, 0, 50)},    // Level 9: Magenta gradients
        {new Color(50, 100, 150), new Color(0, 50, 100)}     // Level 10: Sky blue gradients
    };

    public static void drawBackground(Graphics2D g2d, int width, int height, int level) {
        // Make sure level is within bounds (1-based index)
        int colorIndex = Math.max(0, Math.min(level - 1, LEVEL_COLORS.length - 1));
        Color[] colors = LEVEL_COLORS[colorIndex];
        
        // Create gradient paint
        GradientPaint gradient = new GradientPaint(
            0, 0, colors[0],
            width, height, colors[1]
        );
        
        // Fill background with gradient
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        
        // Add subtle pattern for texture
        drawBackgroundPattern(g2d, width, height, colors[1]);
    }

    private static void drawBackgroundPattern(Graphics2D g2d, int width, int height, Color baseColor) {
        // Save original composite
        Composite originalComposite = g2d.getComposite();
        
        // Set very subtle transparency for pattern
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
        g2d.setColor(new Color(255, 255, 255));
        
        // Draw subtle diagonal lines
        int spacing = 20;
        for (int i = -height; i < width + height; i += spacing) {
            g2d.drawLine(i, 0, i + height, height);
        }
        
        // Restore original composite
        g2d.setComposite(originalComposite);
    }
}