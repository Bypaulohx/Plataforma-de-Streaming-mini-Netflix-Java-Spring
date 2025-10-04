async function loadVideos() {
  const res = await fetch('/api/videos');
  const videos = await res.json();
  const container = document.getElementById('videos');
  container.innerHTML = '';
  if (videos.length === 0) {
    container.innerHTML = '<p>Nenhum vídeo enviado ainda.</p>';
    return;
  }
  videos.forEach(v => {
    const card = document.createElement('div');
    card.className = 'video-card';
    card.innerHTML = `
      <h3>${v.title}</h3>
      <p>${v.description || ''}</p>
      <video controls width="640" src="/api/videos/${v.id}/stream"></video>
      <p><a href="/api/videos/${v.id}/download">Baixar</a></p>
    `;
    container.appendChild(card);
  });
}

document.getElementById('uploadForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const fileInput = document.getElementById('file');
  if (!fileInput.files.length) return alert('Selecione um arquivo');
  const fd = new FormData();
  fd.append('file', fileInput.files[0]);
  fd.append('title', document.getElementById('title').value);
  fd.append('description', document.getElementById('description').value);

  const message = document.getElementById('message');
  message.textContent = 'Enviando...';

  const res = await fetch('/api/videos', { method: 'POST', body: fd });
  if (!res.ok) {
    message.textContent = 'Falha no upload';
    return;
  }
  message.textContent = 'Upload concluído!';
  fileInput.value = '';
  document.getElementById('title').value = '';
  document.getElementById('description').value = '';
  await loadVideos();
});

// load on start
loadVideos();
