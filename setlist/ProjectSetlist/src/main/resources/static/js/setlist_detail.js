document.querySelector('textarea[name="content"]').addEventListener('input', function () {
    this.style.height = 'auto'; // Reset height to calculate new size
    this.style.height = this.scrollHeight + 'px'; // Adjust height to fit content
});
