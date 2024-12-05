INSERT INTO artists (description, image_filename, name) 
VALUES 
('A legendary rock artist', 'rock_artist.jpg', 'Taylor Swift')


INSERT INTO shows (id, city, country, date, venue, artist_id) 
VALUES 
(1, 'Los Angeles', 'USA', '2024-12-10', 'Staples Center', 1);

Select * from roles

INSERT INTO setlists (id, proof_filename, show_id) 
VALUES 
(1, 'apa.jpg', 1);

INSERT INTO setlist_songs (setlist_id, song) 
VALUES 
(1, 'Cruel Summer'),
(1, 'Everything has change'),
(1, 'Fortnight');