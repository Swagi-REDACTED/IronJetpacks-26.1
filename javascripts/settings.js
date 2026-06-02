document.addEventListener('DOMContentLoaded', () => {
    // 1. State Management
    const state = {
        boldText: localStorage.getItem('mc_bold_text') === 'true', // Default false
        engineActive: localStorage.getItem('mc_engine_active') === 'true', // Default false
        hoverActive: localStorage.getItem('mc_hover_active') === 'true', // Default false
        engineKey: localStorage.getItem('mc_engine_key') || 'V',
        hoverKey: localStorage.getItem('mc_hover_key') || 'G'
    };

    function saveState() {
        localStorage.setItem('mc_bold_text', state.boldText);
        localStorage.setItem('mc_engine_active', state.engineActive);
        localStorage.setItem('mc_hover_active', state.hoverActive);
        localStorage.setItem('mc_engine_key', state.engineKey);
        localStorage.setItem('mc_hover_key', state.hoverKey);
        applyGlobalClasses();
        // Dispatch custom event to notify tooltips.js
        window.dispatchEvent(new CustomEvent('mcSettingsChanged'));
    }

    function applyGlobalClasses() {
        if (!state.boldText) {
            document.body.classList.add('mc-no-bold');
        } else {
            document.body.classList.remove('mc-no-bold');
        }
    }

    // Apply initially
    applyGlobalClasses();

    // Export state globally so tooltips.js can read it
    window.mcSettings = state;

    // 2. DOM Injection
    const searchContainer = document.querySelector('.md-search');
    if (!searchContainer) return;

    // Button
    const btn = document.createElement('div');
    btn.className = 'mc-settings-btn';
    btn.innerHTML = `
        <svg viewBox="0 0 24 24">
            <line class="line-top" x1="4" y1="6" x2="20" y2="6"/>
            <line class="line-middle" x1="4" y1="12" x2="20" y2="12"/>
            <line class="line-bottom" x1="4" y1="18" x2="20" y2="18"/>
        </svg>
    `;

    // Panel
    const panel = document.createElement('div');
    panel.className = 'mc-settings-panel';
    panel.innerHTML = `
        <div class="mc-setting-item">
            <span>Bold Tooltip Text</span>
            <div class="mc-toggle ${state.boldText ? 'active' : ''}" id="toggle-bold"></div>
        </div>
        <div class="mc-setting-item">
            <span>Active Engine</span>
            <div style="display:flex; align-items:center; gap:8px;">
                <div class="mc-keybind-widget" id="key-engine">${state.engineKey}</div>
                <div class="mc-toggle ${state.engineActive ? 'active' : ''}" id="toggle-engine"></div>
            </div>
        </div>
        <div class="mc-setting-item">
            <span>Active Hover</span>
            <div style="display:flex; align-items:center; gap:8px;">
                <div class="mc-keybind-widget" id="key-hover">${state.hoverKey}</div>
                <div class="mc-toggle ${state.hoverActive ? 'active' : ''}" id="toggle-hover"></div>
            </div>
        </div>
    `;

    // Insert before the search input but inside the search wrapper, 
    // or insert it directly before the search container.
    searchContainer.parentNode.insertBefore(btn, searchContainer);
    // Append panel to the button wrapper so it drops down from there
    btn.style.position = 'relative';
    btn.appendChild(panel);

    // Make the header layout flex to fit the button
    if (searchContainer.parentNode) {
        searchContainer.parentNode.style.display = 'flex';
        searchContainer.parentNode.style.alignItems = 'center';
    }

    // 3. Interactions
    btn.addEventListener('click', (e) => {
        // Prevent toggle if clicking inside panel
        if (e.target.closest('.mc-settings-panel')) return;
        
        btn.classList.toggle('active');
        panel.classList.toggle('active');
    });

    // Toggles
    document.getElementById('toggle-bold').addEventListener('click', function() {
        this.classList.toggle('active');
        state.boldText = this.classList.contains('active');
        saveState();
    });

    const toggleEngine = document.getElementById('toggle-engine');
    toggleEngine.addEventListener('click', function() {
        this.classList.toggle('active');
        state.engineActive = this.classList.contains('active');
        saveState();
    });

    const toggleHover = document.getElementById('toggle-hover');
    toggleHover.addEventListener('click', function() {
        this.classList.toggle('active');
        state.hoverActive = this.classList.contains('active');
        saveState();
    });

    // Keybinds
    let listeningWidget = null;
    let listeningType = null;

    function startListening(widgetId, type) {
        if (listeningWidget) return;
        const widget = document.getElementById(widgetId);
        listeningWidget = widget;
        listeningType = type;
        widget.classList.add('listening');
        widget.innerText = '...';
    }

    function stopListening(key) {
        if (!listeningWidget) return;
        
        if (key) {
            let displayKey = key;
            if (key === ' ') displayKey = 'SPACE';
            
            listeningWidget.innerText = displayKey.toUpperCase();
            if (listeningType === 'engine') state.engineKey = displayKey.toUpperCase();
            if (listeningType === 'hover') state.hoverKey = displayKey.toUpperCase();
            saveState();
        } else {
            // Restore previous if cancelled
            listeningWidget.innerText = (listeningType === 'engine' ? state.engineKey : state.hoverKey);
        }
        
        listeningWidget.classList.remove('listening');
        listeningWidget = null;
        listeningType = null;
    }

    document.getElementById('key-engine').addEventListener('click', () => startListening('key-engine', 'engine'));
    document.getElementById('key-hover').addEventListener('click', () => startListening('key-hover', 'hover'));

    // Global Key Listener for assigning AND triggering
    document.addEventListener('keydown', (e) => {
        if (listeningWidget) {
            e.preventDefault();
            stopListening(e.key);
            return;
        }

        // Trigger toggles if matching key is pressed (ignore if inside an input)
        if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA') return;

        const key = e.key.toUpperCase();
        if (key === state.engineKey) {
            toggleEngine.click();
        } else if (key === state.hoverKey) {
            toggleHover.click();
        }
    });

    // Mouse bindings (Mouse4/Mouse5)
    document.addEventListener('mousedown', (e) => {
        if (listeningWidget) {
            e.preventDefault();
            let btnName = `M${e.button}`;
            stopListening(btnName);
        } else {
            // Trigger toggles for mouse buttons
            let btnName = `M${e.button}`;
            if (btnName === state.engineKey) toggleEngine.click();
            else if (btnName === state.hoverKey) toggleHover.click();
        }
    });
});
