async function loadVehicles(filter, typeFilter) {
  let url = '/api/vehicles';
  const params = [];
  if (filter) params.push('brand=' + encodeURIComponent(filter));
  if (typeFilter) params.push('type=' + encodeURIComponent(typeFilter));
  if (params.length) url += '?' + params.join('&');
  const res = await fetch(url);
  const list = await res.json();
  const tbody = document.querySelector('#vehicleTable tbody');
  tbody.innerHTML = '';
  list.forEach(v => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${v.id}</td>
      <td>${v.type}</td>
      <td>${v.brand}</td>
      <td>${v.model}</td>
      <td>${v.year}</td>
      <td>${(v.details) ? v.details : ''}</td>
      <td>
        <button data-id="${v.id}" class="edit">Edit</button>
        <button data-id="${v.id}" class="delete">Delete</button>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

  let currentEditId = null;
async function init() {
  await loadVehicles();
  // Show/hide ID column
  const idColIdx = 0; // first column
  function setShowIds(val) { document.querySelectorAll('#vehicleTable thead th')[idColIdx].style.display = val ? '' : 'none'; document.querySelectorAll('#vehicleTable tbody td:nth-child(1)').forEach(td => td.style.display = val ? '' : 'none'); }
  document.getElementById('showIds').addEventListener('change', (e) => setShowIds(e.target.checked));
  setShowIds(false);
  document.getElementById('filterBtn').onclick = () => loadVehicles(document.getElementById('brandFilter').value, document.getElementById('typeFilter').value);
  document.getElementById('clearFilterBtn').onclick = () => { document.getElementById('brandFilter').value = ''; document.getElementById('typeFilter').value = ''; loadVehicles(); };
  // CSV load/save removed: use JSON endpoints exclusively
  document.getElementById('loadJsonBtn').onclick = async () => { await fetch('/api/vehicles/loadJson', {method: 'POST'}); await loadVehicles(); };
  document.getElementById('saveJsonBtn').onclick = async () => { await fetch('/api/vehicles/saveJson', {method: 'POST'}); alert('Saved JSON'); };

  async function handleAddFormSubmit(e) {
    e.preventDefault();
    const type = document.getElementById('typeSelect').value.toLowerCase();
    const brand = document.getElementById('brand').value;
    const model = document.getElementById('model').value;
    const year = document.getElementById('year').value;
    // collect type-specific fields
    let params = new URLSearchParams();
    params.set('type', type);
    params.set('brand', brand);
    params.set('model', model);
    params.set('year', year);
    switch (type) {
      case 'car': params.set('doors', document.getElementById('doors').value || '4'); params.set('fuel', document.getElementById('fuel').value || 'Petrol'); break;
      case 'bike': params.set('sidecar', document.getElementById('sidecar').checked); params.set('category', document.getElementById('bikeCategory').value || 'Cruiser'); break;
      case 'truck': params.set('payload', document.getElementById('payload').value || '0'); params.set('trailer', document.getElementById('trailer').checked); break;
      case 'motorcycle': params.set('cc', document.getElementById('cc').value || '500'); params.set('category', document.getElementById('motorCategory').value || 'Sports'); break;
    }
    if (currentEditId) {
      params.set('id', currentEditId);
      // Use update endpoint with POST to support servers without method override
      await fetch('/api/vehicles/update', { method: 'POST', body: params });
      currentEditId = null;
      document.querySelector('#addForm button[type=submit]').textContent = 'Add';
    } else {
      await fetch('/api/vehicles/add', { method: 'POST', body: params });
    }
    document.getElementById('brand').value = '';
    document.getElementById('model').value = '';
    document.getElementById('year').value = '';
    document.getElementById('extra').value = '';
    await loadVehicles();
  }
  document.querySelector('#addForm').addEventListener('submit', handleAddFormSubmit);

  document.querySelector('#vehicleTable').addEventListener('click', async (e) => {
    if (e.target.classList.contains('delete')) {
      const id = e.target.getAttribute('data-id');
      if (!confirm('Delete vehicle with id ' + id + '?')) return;
      await fetch('/api/vehicles/delete', { method: 'POST', body: new URLSearchParams({id}) });
      await loadVehicles();
    }
    if (e.target.classList.contains('edit')) {
      const id = e.target.getAttribute('data-id');
      // fetch vehicle
      const res = await fetch('/api/vehicles/' + id);
      const v = await res.json();
      // populate form for editing
      document.getElementById('typeSelect').value = v.type;
      document.getElementById('brand').value = v.brand;
      document.getElementById('model').value = v.model;
      document.getElementById('year').value = v.year;
      // fill type-specific fields
      if (v.type === 'Car') { document.getElementById('doors').value = v.doors; document.getElementById('fuel').value = v.fuel; }
      if (v.type === 'Bike') { document.getElementById('sidecar').checked = v.sidecar; document.getElementById('bikeCategory').value = v.category; }
      if (v.type === 'Truck') { document.getElementById('payload').value = v.payload; document.getElementById('trailer').checked = v.trailer; }
      if (v.type === 'Motorcycle') { document.getElementById('cc').value = v.cc; document.getElementById('motorCategory').value = v.category; }
      // change Add to Save and handle update
      document.querySelector('#addForm button[type=submit]').textContent = 'Save';
      currentEditId = id;
      // show modal
      document.getElementById('modal').style.display = 'block';
    }
  });

  // A helper to render type-specific fields
  function renderTypeFields(type) {
    const container = document.getElementById('typeSpecific');
    container.innerHTML = '';
    switch (type.toLowerCase()) {
      case 'car':
        container.innerHTML = `<label>Doors: <input id="doors" type="number" min="1" value="4"></label> <label>Fuel: <input id="fuel" value="Petrol"></label>`;
        break;
      case 'bike':
        container.innerHTML = `<label>Sidecar: <input id="sidecar" type="checkbox"></label> <label>Category: <input id="bikeCategory" value="Cruiser"></label>`;
        break;
      case 'truck':
        container.innerHTML = `<label>Payload (kg): <input id="payload" type="number" min="0" value="0"></label> <label>Trailer: <input id="trailer" type="checkbox"></label>`;
        break;
      case 'motorcycle':
        container.innerHTML = `<label>Engine CC: <input id="cc" type="number" min="50" value="500"></label> <label>Category: <input id="motorCategory" value="Sports"></label>`;
        break;
      default:
        container.innerHTML = '';
    }
  }

  // Modal open/close
  document.getElementById('addBtn').addEventListener('click', () => { currentEditId = null; document.querySelector('#addForm').reset(); renderTypeFields('Car'); document.getElementById('modalTitle').textContent = 'Add Vehicle'; document.querySelector('#addForm button[type=submit]').textContent = 'Add'; document.getElementById('modal').style.display = 'block'; });
  document.getElementById('cancelBtn').addEventListener('click', () => { document.getElementById('modal').style.display = 'none'; });
  document.getElementById('typeSelect').addEventListener('change', (e) => renderTypeFields(e.target.value));
  // render initial type fields
  renderTypeFields(document.getElementById('typeSelect').value);
}

init();
