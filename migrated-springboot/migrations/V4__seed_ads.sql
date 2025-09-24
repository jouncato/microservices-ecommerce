-- V4__seed_ads.sql
-- Seed ads data for ad service

INSERT INTO ads (context_key, redirect_url, text, active) VALUES
('electronics', 'https://example.com/electronics-sale', 'Electronics Sale - Up to 50% Off!', true),
('clothing', 'https://example.com/fashion-trends', 'New Fashion Trends - Shop Now!', true),
('kitchen', 'https://example.com/kitchen-essentials', 'Kitchen Essentials - Free Shipping!', true),
('accessories', 'https://example.com/accessories', 'Stylish Accessories - Limited Time!', true),
('footwear', 'https://example.com/shoes', 'Comfortable Shoes - Best Prices!', true),
('beauty', 'https://example.com/beauty-products', 'Beauty Products - Premium Quality!', true),
('home', 'https://example.com/home-decor', 'Home Decor - Transform Your Space!', true),
('vintage', 'https://example.com/vintage-collection', 'Vintage Collection - Unique Finds!', true);
