document.addEventListener('DOMContentLoaded', () => {
    let recipesData = null;
    let hitboxesData = null;
    let furnaceHitboxesData = null;
    let tooltipEl = null;
    let activeItem = null;
    let isShiftPressed = false;

    // Track Shift key state
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Shift') {
            isShiftPressed = true;
            if (activeItem) {
                tooltipEl.innerHTML = renderTooltipContent(activeItem);
            }
        }
    });

    document.addEventListener('keyup', (e) => {
        if (e.key === 'Shift') {
            isShiftPressed = false;
            if (activeItem) {
                tooltipEl.innerHTML = renderTooltipContent(activeItem);
            }
        }
    });

    // Dynamically calculate the base URL from our own script tag
    // MkDocs generates relative paths like '../javascripts/tooltips.js'
    let scriptTag = document.querySelector('script[src*="javascripts/tooltips.js"]');
    let baseUrl = scriptTag ? scriptTag.getAttribute('src').split('javascripts/tooltips.js')[0] : '';
    // Ensure it ends with slash if it's not empty and doesn't already
    if (baseUrl && !baseUrl.endsWith('/')) baseUrl += '/';

    // Fetch the JSON files using the relative base path
    Promise.all([
        fetch(baseUrl + 'javascripts/recipes_data.json').then(r => r.json()),
        fetch(baseUrl + 'image/Vanilla/Crafting-Inventory-Hitboxes.json').then(r => r.json()),
        fetch(baseUrl + 'image/Vanilla/Furnace-Inventory-Hitboxes.json').then(r => r.json())
    ]).then(([recipes, hitboxes, furnaceHitboxes]) => {
        recipesData = recipes;
        hitboxesData = hitboxes;
        furnaceHitboxesData = furnaceHitboxes;
        initTooltips();
    }).catch(e => console.error("Failed to load tooltip data", e));

    function createTooltipElement() {
        tooltipEl = document.createElement('div');
        tooltipEl.className = 'mc-tooltip';
        document.body.appendChild(tooltipEl);
    }

    // Global mouse tracking for scroll detection
    let mouseX = 0;
    let mouseY = 0;
    
    document.addEventListener('mousemove', (e) => {
        mouseX = e.clientX;
        mouseY = e.clientY;
    });

    // Listen for setting changes
    window.addEventListener('mcSettingsChanged', () => {
        if (activeItem && tooltipEl && tooltipEl.style.display !== 'none') {
            tooltipEl.innerHTML = renderTooltipContent(activeItem);
        }
    });

    // Handle scroll by dispatching a synthetic mousemove event so existing listeners catch it natively
    window.addEventListener('scroll', () => {
        const el = document.elementFromPoint(mouseX, mouseY);
        if (el) {
            if (!el.classList.contains('mc-hover-zone')) {
                if (activeItem && tooltipEl) {
                    tooltipEl.style.display = 'none';
                    activeItem = null;
                }
            } else {
                const ev = new MouseEvent('mousemove', {
                    view: window,
                    bubbles: true,
                    cancelable: true,
                    clientX: mouseX,
                    clientY: mouseY
                });
                el.dispatchEvent(ev);
            }
        }
    }, { passive: true, capture: true });

    function renderTooltipContent(item) {
        if (!item) return '';
        let html = `<div class="mc-title">${item.name}</div>`;
        
        if (item.isJetpack && item.stats) {
            const engineClass = (window.mcSettings && !window.mcSettings.engineActive) ? 'text-red' : 'text-green';
            const hoverClass = (window.mcSettings && !window.mcSettings.hoverActive) ? 'text-red' : 'text-green';

            html += `<div class="mc-subtitle">0 E / ${item.stats.capacity} E</div>`;
            html += `<div class="mc-badges">
                <span class="text-gray">TIER ${item.tier}</span> <span class="text-gray">|</span> 
                <span class="${engineClass}">ENGINE</span> <span class="text-gray">|</span> 
                <span class="${hoverClass}">HOVER</span>
            </div>`;
            html += `<div class="mc-separator"></div>`;
            
            if (isShiftPressed) {
                // Extended stats view
                html += `<div class="mc-stats">
                    <div class="text-gray">Fuel Usage: <span class="text-white">${item.stats.fuel}</span></div>
                    <div class="text-gray">Vertical Speed: <span class="text-white">${item.stats.speed}</span></div>
                    <div class="text-gray">Vertical Acceleration: <span class="text-white">${item.stats.vAccel}</span></div>
                    <div class="text-gray">Horizontal Speed: <span class="text-white">${item.stats.hSpeed}</span></div>
                    <div class="text-gray">Hover Speed: <span class="text-white">${item.stats.hover}</span></div>
                    <div class="text-gray">Descend Speed: <span class="text-white">${item.stats.descend}</span></div>
                    <div class="text-gray">Sprint Modifier: <span class="text-white">${item.stats.sprintMod}</span></div>
                    <div class="text-gray">Sprint Fuel Modifier: <span class="text-white">${item.stats.sprintFuel}</span></div>
                </div>`;
            } else {
                // Short view
                html += `<div class="mc-footer text-gray">
                    Hold <span class="text-yellow mc-italic">SHIFT</span>&nbsp;for info
                </div>`;
            }
            
            html += `<div class="mc-separator"></div>`;
            html += `<div class="text-gray">When on Chest:</div>`;
            html += `<div class="text-blue">${item.stats.armor} Armor</div>`;
        } else if (item.tags) {
            item.tags.forEach(tag => {
                html += `<div class="text-blue">${tag}</div>`;
            });
        }
        return html;
    }

    function initTooltips() {
        createTooltipElement();
        
        const recipeResizeObserver = new ResizeObserver(entries => {
            for (let entry of entries) {
                const parentWidth = entry.contentRect.width;
                const wrappers = entry.target.querySelectorAll('.crafting-container, .furnace-container');
                
                wrappers.forEach(wrapper => {
                    const img = wrapper.querySelector('img');
                    if (!img) return;
                    
                    const nativeW = 352;
                    const nativeH = 332;
                    
                    let scale = 1.0;
                    if (parentWidth < nativeW) {
                        if (parentWidth >= nativeW * 0.75) scale = 0.75;
                        else if (parentWidth >= nativeW * 0.5) scale = 0.5;
                        else scale = 0.25;
                    }
                    
                    const newW = nativeW * scale;
                    const newH = nativeH * scale;
                    
                    wrapper.style.width = newW + 'px';
                    wrapper.style.height = newH + 'px';
                    img.style.width = newW + 'px';
                    img.style.height = newH + 'px';
                });
            }
        });

        const images = document.querySelectorAll('img');
        images.forEach(img => {
            if (!img.src.includes('_recipe')) return;
            
            const filenameMatch = img.src.match(/([^\/]+_recipe\.(png|gif))/);
            if (!filenameMatch) return;
            const filename = filenameMatch[1];
            
            const recipe = recipesData[filename];
            if (!recipe) return;

            const wrapper = document.createElement('div');
            const isFurnace = filename.includes('smelting');
            wrapper.className = isFurnace ? 'furnace-container' : 'crafting-container';
            img.parentNode.insertBefore(wrapper, img);
            wrapper.appendChild(img);
            
            if (wrapper.parentNode) {
                recipeResizeObserver.observe(wrapper.parentNode);
            }
            
            const activeHitboxes = isFurnace ? furnaceHitboxesData : hitboxesData;

            activeHitboxes.forEach(hb => {
                let slotId = null;
                if (!isFurnace) {
                    if (hb.label.startsWith('Slot ')) {
                        slotId = String(parseInt(hb.label.split(' ')[1]) - 1);
                    } else if (hb.label === 'Output') {
                        slotId = 'output';
                    }
                } else {
                    if (hb.label === 'Input' || hb.label === 'Fuel' || hb.label === 'Output') {
                        slotId = hb.label;
                    }
                }

                if (slotId && recipe[slotId]) {
                    const zone = document.createElement('div');
                    zone.className = 'mc-hover-zone';
                    
                    // Render using percentages so hitboxes perfectly scale even on mobile or compressed layouts
                    zone.style.left = ((hb.x / 352) * 100) + '%';
                    zone.style.top = ((hb.y / 332) * 100) + '%';
                    zone.style.width = ((hb.width / 352) * 100) + '%';
                    zone.style.height = ((hb.height / 332) * 100) + '%';

                    zone.addEventListener('mouseenter', () => {
                        activeItem = recipe[slotId];
                        tooltipEl.innerHTML = renderTooltipContent(activeItem);
                        tooltipEl.style.display = 'block';
                    });

                    // Use clientX/clientY for fixed positioning so scrolling doesn't detach the tooltip
                    zone.addEventListener('mousemove', (e) => {
                        // Ensure tooltip is active if a synthetic event (or fast mouse) skipped mouseenter
                        if (activeItem !== recipe[slotId]) {
                            activeItem = recipe[slotId];
                            tooltipEl.innerHTML = renderTooltipContent(activeItem);
                            tooltipEl.style.display = 'block';
                        }
                        tooltipEl.style.left = (e.clientX + 16) + 'px';
                        tooltipEl.style.top = (e.clientY - 32) + 'px';
                    });

                    zone.addEventListener('mouseleave', () => {
                        activeItem = null;
                        tooltipEl.style.display = 'none';
                    });

                    wrapper.appendChild(zone);
                }
            });
        });
    }
});
