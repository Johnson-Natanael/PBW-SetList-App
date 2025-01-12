// Fungsi untuk memvalidasi input komentar
document.getElementById('commentArea').addEventListener('input', function() {
    var commentContent = document.getElementById('commentArea').value.trim();  // Ambil nilai textarea
    var submitButton = document.querySelector('.submit-btn');  // Ambil tombol submit

    if (commentContent === "") {
        // Jika komentar kosong, beri gaya hitam dan nonaktifkan tombol
        submitButton.style.backgroundColor = "black";
        submitButton.disabled = true;
    } else {
        // Jika ada komentar, kembalikan gaya normal dan aktifkan tombol
        submitButton.style.backgroundColor = ""; // Reset warna tombol
        submitButton.disabled = false;
    }
});

// Fungsi untuk membuat ukuran area untuk melakukan komentar menjadi dynamic
document.querySelector('textarea[name="content"]').addEventListener('input', function () {
    this.style.height = 'auto'; // Reset height to calculate new size
});
