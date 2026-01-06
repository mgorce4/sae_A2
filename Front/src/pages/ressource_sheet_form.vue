<script setup>
import axios from 'axios'
import { status } from '../main'
import { onMounted, ref, nextTick, computed } from 'vue'

status.value = "Professeur"

/* Extract ID from hash URL parameters */
const getQueryParam = (param) => {
  const hash = window.location.hash
  console.log('Full hash:', hash)

  // Hash format: #/form-ressource-sheet?id=5
  const queryString = hash.split('?')[1]
  console.log('Query string:', queryString)

  if (!queryString) {
    console.warn('No query string found in hash')
    return null
  }

  const params = new URLSearchParams(queryString)
  const value = params.get(param)
  console.log(`Extracted ${param}:`, value)
  return value
}

const resourceSheetId = ref(getQueryParam('id'))
console.log('Parsed resourceSheetId:', resourceSheetId.value)

/* link with the API */
const resourceSheet = ref(null)
const resource = ref(null)
const ueLabels = ref([])
const institutionName = ref(null)
const saeList = ref([]) // List of SAEs from the same semester
const linkedSaeIds = ref([]) // IDs of SAEs linked to this resource
const localSaeChanges = ref({}) // Local state to track SAE switch changes
const nationalProgramObjectives = ref([]) // List of objectives from the database
const localObjectiveContent = ref('') // Local state for objective textarea
const nationalProgramSkills = ref([]) // List of skills from the database
const localSkills = ref([]) // Local state for skills array (with label and description)
const keywords = ref([]) // List of keywords from the database
const localKeywords = ref([]) // Local state for keywords array
const modalities = ref([]) // List of modalities from the database
const localModalities = ref([]) // Local state for modalities array
const pedagogicalContent = ref(null) // Pedagogical content from the database
const localPedagogicalContent = ref({
  cm: [], // Array of { number: 1, content: 'text' }
  td: [],
  tp: []
})

// Function to parse CSV string into array of items
const parseCSVContent = (csvString) => {
  console.log('🔍 Parsing CSV string:', csvString)

  if (!csvString || csvString.trim() === '') {
    console.log('⚠️ CSV string is empty or null')
    return []
  }

  // Split by comma followed by a number and a space
  // Format: "1 text,2 text,3 text"
  const items = []
  const regex = /(\d+)\s+([^,]+)(?:,|$)/g
  let match

  while ((match = regex.exec(csvString)) !== null) {
    const item = {
      number: parseInt(match[1]),
      content: match[2].replace(/''/g, "'").trim() // Replace double quotes with single
    }
    console.log('✅ Parsed item:', item)
    items.push(item)
  }

  console.log('📊 Total items parsed:', items.length)
  return items
}

// Function to convert array back to CSV string (for saving to database)
const toCSVContent = (items) => {
  if (!items || items.length === 0) return ''

  return items
    .map(item => `${item.number} ${item.content.replace(/'/g, "''")}`)
    .join(',')
}

// Function to add a new pedagogical content item
const addPedagogicalItem = (type) => {
  const list = localPedagogicalContent.value[type]
  const nextNumber = list.length > 0 ? Math.max(...list.map(item => item.number)) + 1 : 1

  list.push({
    number: nextNumber,
    content: ''
  })

  console.log(`New ${type} item added. Total:`, list.length)

  // Update panel height after adding
  nextTick(() => {
    const pedagogicalPanel = document.querySelector('.pedagogical-content')?.closest('.panel')
    if (pedagogicalPanel) {
      updatePanelHeight(pedagogicalPanel)
    }
  })
}

// Function to remove a pedagogical content item
const removePedagogicalItem = (type, index) => {
  localPedagogicalContent.value[type].splice(index, 1)
  console.log(`${type} item removed. Total:`, localPedagogicalContent.value[type].length)

  // Update panel height after removing
  nextTick(() => {
    const pedagogicalPanel = document.querySelector('.pedagogical-content')?.closest('.panel')
    if (pedagogicalPanel) {
      updatePanelHeight(pedagogicalPanel)
    }
  })
}

// Function for return button
const goBack = () => {
  window.location.hash = '#/teacher-dashboard'
}

// Function to add a new keyword
const addKeyword = () => {
  localKeywords.value.push({ keyword: '', isNew: true })
  console.log('New keyword added. Total keywords:', localKeywords.value.length)
}

// Function to remove a keyword
const removeKeyword = (index) => {
  localKeywords.value.splice(index, 1)
  console.log('Keyword removed. Total keywords:', localKeywords.value.length)
}

// Function to handle Enter key in keyword input
const handleKeywordEnter = (index) => {
  // Only add a new keyword if the current one is not empty
  if (localKeywords.value[index].keyword.trim() !== '') {
    addKeyword()
    // Focus on the new input after a short delay
    nextTick(() => {
      const inputs = document.querySelectorAll('.keyword-input')
      if (inputs[index + 1]) {
        inputs[index + 1].focus()
      }
    })
  }
}

// Function to add a new modality
const addModality = () => {
  localModalities.value.push({ modality: '', isNew: true })
  console.log('New modality added. Total modalities:', localModalities.value.length)

  // Update panel height after adding
  nextTick(() => {
    const modalitiesPanel = document.querySelector('.modalities-list')?.closest('.panel')
    if (modalitiesPanel) {
      updatePanelHeight(modalitiesPanel)
    }
  })
}

// Function to remove a modality
const removeModality = (index) => {
  localModalities.value.splice(index, 1)
  console.log('Modality removed. Total modalities:', localModalities.value.length)

  // Update panel height after removing
  nextTick(() => {
    const modalitiesPanel = document.querySelector('.modalities-list')?.closest('.panel')
    if (modalitiesPanel) {
      updatePanelHeight(modalitiesPanel)
    }
  })
}

// Function to check if a SAE is linked to the resource (including local changes)
const isSaeLinked = (saeId) => {
  // Check if there's a local change, otherwise use the original linked state
  if (saeId in localSaeChanges.value) {
    return localSaeChanges.value[saeId]
  }
  return linkedSaeIds.value.includes(saeId)
}

// Function to toggle SAE link state locally
const toggleSaeLink = (saeId) => {
  const currentState = isSaeLinked(saeId)
  localSaeChanges.value[saeId] = !currentState
  console.log('SAE', saeId, 'toggled to:', !currentState)
  console.log('Local changes:', localSaeChanges.value)
}

// Function to update panel max-height after content changes
const updatePanelHeight = (panelElement) => {
  if (panelElement && panelElement.style.maxHeight) {
    // Use nextTick to wait for DOM update
    nextTick(() => {
      panelElement.style.maxHeight = panelElement.scrollHeight + 'px'
    })
  }
}

// Function to add a new skill row
const addSkillRow = () => {
  localSkills.value.push({ id: null, label: '', description: '' })
  console.log('New skill row added. Total skills:', localSkills.value.length)

  // Update panel height after adding
  nextTick(() => {
    const skillsPanel = document.querySelector('.skills-table')?.closest('.panel')
    if (skillsPanel) {
      updatePanelHeight(skillsPanel)
    }
  })
}

// Function to remove a skill row
const removeSkillRow = (index) => {
  if (localSkills.value.length > 1) {
    localSkills.value.splice(index, 1)
    console.log('Skill row removed. Total skills:', localSkills.value.length)

    // Update panel height after removing
    nextTick(() => {
      const skillsPanel = document.querySelector('.skills-table')?.closest('.panel')
      if (skillsPanel) {
        updatePanelHeight(skillsPanel)
      }
    })
  }
}

// Computed property to check if objective content has been modified
const hasObjectiveChanges = computed(() => {
  if (!nationalProgramObjectives.value || nationalProgramObjectives.value.length === 0) {
    return localObjectiveContent.value.trim() !== ''
  }

  const originalContent = nationalProgramObjectives.value
    .map(obj => obj.content)
    .join('\n\n')

  return localObjectiveContent.value !== originalContent
})

onMounted(async () => {
  console.log('Component mounted, resourceSheetId:', resourceSheetId.value)

  // Get institution name from localStorage
  const storedInstitution = localStorage.getItem('institutionName')
  console.log('Stored institution in localStorage:', storedInstitution)
  if (storedInstitution) {
    institutionName.value = storedInstitution
    console.log('Institution from localStorage:', institutionName.value)
  } else {
    console.warn('No institution found in localStorage')
  }

  /* get the specific resource sheet from the DB using the ID */
  if (resourceSheetId.value) {
    try {
      console.log('Fetching resource sheet with ID:', resourceSheetId.value)
      const response = await axios.get(`http://localhost:8080/api/resource-sheets/${resourceSheetId.value}/details`)
      resourceSheet.value = response.data
      console.log('ResourceSheet data with details:', resourceSheet.value)

      // Get national program objectives for this resource sheet
      if (resourceSheetId.value) {
        try {
          console.log('Fetching national program objectives for resource sheet ID:', resourceSheetId.value)
          const objectivesResponse = await axios.get(`http://localhost:8080/api/national-program-objectives/ressource-sheet/${resourceSheetId.value}`)
          nationalProgramObjectives.value = objectivesResponse.data
          console.log('National program objectives:', nationalProgramObjectives.value)

          // Set the local content from the first objective (or combine multiple)
          if (nationalProgramObjectives.value.length > 0) {
            localObjectiveContent.value = nationalProgramObjectives.value
              .map(obj => obj.content)
              .join('\n\n')
          }
          console.log('Local objective content initialized:', localObjectiveContent.value)
        } catch (error) {
          console.error('Error fetching national program objectives:', error)
        }

        // Get national program skills for this resource sheet
        try {
          console.log('Fetching national program skills for resource sheet ID:', resourceSheetId.value)
          const skillsResponse = await axios.get(`http://localhost:8080/api/national-program-skills/resource-sheet/${resourceSheetId.value}`)
          nationalProgramSkills.value = skillsResponse.data
          console.log('National program skills:', nationalProgramSkills.value)

          // Initialize local skills array from database
          if (nationalProgramSkills.value.length > 0) {
            localSkills.value = nationalProgramSkills.value.map(skill => ({
              id: skill.idSkill,
              label: skill.label || '',
              description: skill.description || ''
            }))
          } else {
            // Start with one empty skill if none exist
            localSkills.value = [{ id: null, label: '', description: '' }]
          }
          console.log('Local skills initialized:', localSkills.value)
        } catch (error) {
          console.error('Error fetching national program skills:', error)
          // Initialize with one empty skill on error
          localSkills.value = [{ id: null, label: '', description: '' }]
        }

        // Get keywords for this resource sheet
        try {
          console.log('Fetching keywords for resource sheet ID:', resourceSheetId.value)
          const keywordsResponse = await axios.get(`http://localhost:8080/api/keywords/resource-sheet/${resourceSheetId.value}`)
          keywords.value = keywordsResponse.data
          console.log('Keywords:', keywords.value)

          // Initialize local keywords array from database
          localKeywords.value = keywords.value.map(kw => ({
            keyword: kw.keyword || '',
            isNew: false
          }))
          console.log('Local keywords initialized:', localKeywords.value)
        } catch (error) {
          console.error('Error fetching keywords:', error)
          // Initialize with empty array on error
          localKeywords.value = []
        }

        // Get modalities of implementation for this resource sheet
        try {
          console.log('Fetching modalities for resource sheet ID:', resourceSheetId.value)
          const modalitiesResponse = await axios.get(`http://localhost:8080/api/modalities-of-implementation/resource-sheet/${resourceSheetId.value}`)
          modalities.value = modalitiesResponse.data
          console.log('Modalities:', modalities.value)

          // Initialize local modalities array from database
          localModalities.value = modalities.value.map(mod => ({
            modality: mod.modality || '',
            isNew: false
          }))
          console.log('Local modalities initialized:', localModalities.value)
        } catch (error) {
          console.error('Error fetching modalities:', error)
          // Initialize with empty array on error
          localModalities.value = []
        }

        // Get pedagogical content for this resource sheet
        try {
          console.log('📚 Fetching pedagogical content for resource sheet ID:', resourceSheetId.value)
          const pedagogicalResponse = await axios.get(`http://localhost:8080/api/pedagogical-contents/resource-sheet/${resourceSheetId.value}`)

          console.log('📦 Pedagogical API response:', pedagogicalResponse.data)
          console.log('📦 Response length:', pedagogicalResponse.data ? pedagogicalResponse.data.length : 0)

          if (pedagogicalResponse.data && pedagogicalResponse.data.length > 0) {
            pedagogicalContent.value = pedagogicalResponse.data[0] // Take the first one
            console.log('✅ Pedagogical content loaded:', pedagogicalContent.value)
            console.log('  CM raw:', pedagogicalContent.value.cm)
            console.log('  TD raw:', pedagogicalContent.value.td)
            console.log('  TP raw:', pedagogicalContent.value.tp)

            // Parse CSV content into arrays
            localPedagogicalContent.value = {
              cm: parseCSVContent(pedagogicalContent.value.cm),
              td: parseCSVContent(pedagogicalContent.value.td),
              tp: parseCSVContent(pedagogicalContent.value.tp)
            }
            console.log('🎯 Local pedagogical content initialized:', localPedagogicalContent.value)
          } else {
            console.log('⚠️ No pedagogical content found in API response')
            // Initialize with empty arrays
            localPedagogicalContent.value = {
              cm: [],
              td: [],
              tp: []
            }
          }
        } catch (error) {
          console.error('❌ Error fetching pedagogical content:', error)
          console.error('Error details:', error.response?.data || error.message)
          // Initialize with empty arrays on error
          localPedagogicalContent.value = {
            cm: [],
            td: [],
            tp: []
          }
        }
      }

      // Extract resource data
      if (resourceSheet.value.resource) {
        resource.value = resourceSheet.value.resource
        console.log('Resource data:', resource.value)
        console.log('Resource ID:', resource.value.idResource)
        console.log('Resource label:', resource.value.label)
        console.log('Resource apogeeCode:', resource.value.apogeeCode)

        // Get UE labels from all UE coefficients linked to this resource
        if (resource.value.idResource) {
          try {
            console.log('Fetching UE coefficients for resource ID:', resource.value.idResource)

            // Try the specific endpoint first
            try {
              const ueCoeffResponse = await axios.get(`http://localhost:8080/api/ue-coefficients/resource/${resource.value.idResource}`)
              const ueCoefficients = ueCoeffResponse.data
              console.log('UE Coefficients from specific endpoint:', ueCoefficients)

              // Extract UE labels from coefficients
              ueLabels.value = ueCoefficients
                .filter(coeff => coeff.ue && coeff.ue.label)
                .map(coeff => coeff.ue.label)

              console.log('UE Labels extracted:', ueLabels.value)
            } catch (endpointError) {
              console.warn('Specific endpoint failed, trying fallback method:', endpointError.message)

              // Fallback: Get all UE coefficients and filter by resource ID
              const allUeCoeffsResponse = await axios.get('http://localhost:8080/api/ue-coefficients')
              const allUeCoefficients = allUeCoeffsResponse.data
              console.log('All UE Coefficients fetched:', allUeCoefficients)

              // Filter by resource ID
              const filteredCoeffs = allUeCoefficients.filter(coeff =>
                coeff.resource && coeff.resource.idResource === resource.value.idResource
              )
              console.log('Filtered UE Coefficients:', filteredCoeffs)

              // Extract UE labels
              ueLabels.value = filteredCoeffs
                .filter(coeff => coeff.ue && coeff.ue.label)
                .map(coeff => coeff.ue.label)

              console.log('UE Labels extracted from fallback:', ueLabels.value)
            }
          } catch (error) {
            console.error('Error fetching UE coefficients:', error)
            console.error('Error details:', error.response?.data || error.message)
          }
        } else {
          console.warn('No idResource found in resource object')
        }

        // Get SAEs from the same semester and institution
        if (resource.value.semester && resource.value.idResource) {
          try {
            console.log('Resource semester:', resource.value.semester)

            // Get all SAEs
            const allSaeResponse = await axios.get('http://localhost:8080/api/saes')
            console.log('All SAEs fetched:', allSaeResponse.data)

            // Get all resources to filter SAEs by semester and institution
            const allResourcesResponse = await axios.get('http://localhost:8080/api/resources')
            const allResources = allResourcesResponse.data
            console.log('All resources fetched:', allResources.length)

            // Filter resources by same semester and same institution
            const sameSemesterResources = allResources.filter(r =>
              r.semester === resource.value.semester
            )
            console.log('Resources with same semester:', sameSemesterResources.length)

            // Get SAE links for resources in the same semester
            const saeLinkPromises = sameSemesterResources.map(r =>
              axios.get(`http://localhost:8080/api/sae-link-resources/resource/${r.idResource}`)
                .catch(() => ({ data: [] }))
            )
            const saeLinkResponses = await Promise.all(saeLinkPromises)
            const allSaeLinks = saeLinkResponses.flatMap(response => response.data)

            // Get unique SAE IDs from same semester resources
            const sameSemesterSaeIds = [...new Set(allSaeLinks.map(link => link.idSAE))]
            console.log('SAE IDs from same semester:', sameSemesterSaeIds)

            // Filter SAEs to only include those from the same semester
            saeList.value = allSaeResponse.data.filter(sae =>
              sameSemesterSaeIds.includes(sae.idSAE)
            )
            console.log('Filtered SAEs from same semester:', saeList.value)
          } catch (error) {
            console.error('Error fetching SAEs:', error)
          }
        }

        // Get SAE links for this specific resource
        if (resource.value.idResource) {
          try {
            console.log('Fetching SAE links for resource ID:', resource.value.idResource)
            const saeLinkResponse = await axios.get(`http://localhost:8080/api/sae-link-resources/resource/${resource.value.idResource}`)
            linkedSaeIds.value = saeLinkResponse.data.map(link => link.idSAE)
            console.log('Linked SAE IDs:', linkedSaeIds.value)
          } catch (error) {
            console.error('Error fetching SAE links:', error)
          }
        }
      } else {
        console.log('No resource found in resourceSheet')
      }
    } catch (error) {
      console.error('Error fetching resource sheet:', error)
      console.error('Error details:', error.response?.data || error.message)
    }
  } else {
    console.warn('No resourceSheetId found in URL')
  }

  // Wait for DOM to be fully rendered
  await nextTick()

  // Initialize accordion after DOM is ready
  const acc = document.getElementsByClassName("accordion");

  for (let i = 0; i < acc.length; i++) {
    acc[i].addEventListener("click", function() {
      this.classList.toggle("active");
      const panel = this.nextElementSibling;
      if (panel.style.maxHeight) {
        panel.style.maxHeight = null;
        panel.style.padding = "0 18px";
      } else {
        panel.style.padding = "2vw 18px";
        panel.style.maxHeight = panel.scrollHeight + "px";
      }
    });
  }

  // Auto-resize textareas (only those with specific classes)
  const textareas = document.querySelectorAll('#text_area_styled, .auto-resize-textarea, .skill-input-description, .modality-textarea');
  textareas.forEach(textarea => {
    const autoResize = () => {
      textarea.style.height = 'auto';
      textarea.style.height = (textarea.scrollHeight) + 'px';

      // Update parent panel height if inside an accordion
      const panel = textarea.closest('.panel');
      if (panel && panel.style.maxHeight) {
        panel.style.maxHeight = panel.scrollHeight + 'px';
      }
    };

    // Set initial height
    setTimeout(() => autoResize(), 0);

    // Add event listener for input
    textarea.addEventListener('input', autoResize);

    // Add event listener for when accordion opens
    const accordion = textarea.closest('#form')?.querySelector('.accordion');
    if (accordion) {
      accordion.addEventListener('click', () => {
        setTimeout(() => autoResize(), 250);
      });
    }
  });

  // Also add listeners to skill label inputs to update panel height
  const skillInputs = document.querySelectorAll('.skill-input-label');
  skillInputs.forEach(input => {
    input.addEventListener('input', () => {
      const panel = input.closest('.panel');
      if (panel && panel.style.maxHeight) {
        nextTick(() => {
          panel.style.maxHeight = panel.scrollHeight + 'px';
        });
      }
    });
  });

  // Add listeners to pedagogical inputs to update panel height
  const pedagogicalInputs = document.querySelectorAll('.pedagogical-input');
  pedagogicalInputs.forEach(input => {
    input.addEventListener('input', () => {
      const panel = input.closest('.panel');
      if (panel && panel.style.maxHeight) {
        nextTick(() => {
          panel.style.maxHeight = panel.scrollHeight + 'px';
        });
      }
    });
  });
})
</script>
<template>
  <div id="Ressource_Sheet">
    <div id="return_Arrow" @click="goBack">
      <button id="back_arrow">←</button>
      <p>Retour</p>
    </div>
    <div id="background_Form">
      <div class="header_Form">
        <p>Réf. UE : </p>
        <p>{{ ueLabels && ueLabels.length > 0 ? ueLabels.join(', ') : '###' }}</p>
        <p class="title">{{ (resource && resource.name) || (resourceSheet && resourceSheet.name) || 'Nom de la ressource' }}</p>
        <p>Dep : </p>
        <p>{{ institutionName || '###' }}</p>
      </div>
      <div class="ref_Section">
        <p>Réf. ressource : </p>
        <p>{{ (resource && resource.label) || '###' }}</p>
      </div>
      <div id="form">
        <button class="accordion" id="dark_Bar">Objectif de la ressource *</button>
        <div class="panel">
          <textarea id="text_area_styled" v-model="localObjectiveContent" placeholder="Saisissez les objectifs de la ressource..."></textarea>
        </div>
      </div>
      <div id="form">
        <button class="accordion" id="dark_Bar">Compétences *</button>
        <div class="panel">
          <div class="skills-table">
            <div class="skills-header">
              <div class="skill-column-label">Label</div>
              <div class="skill-column-description">Description</div>
            </div>
            <div v-for="(skill, index) in localSkills" :key="index" class="skill-row">
              <div class="skill-inputs">
                <input type="text" v-model="skill.label" placeholder="Label de la compétence..." class="skill-input skill-input-label" />
                <textarea v-model="skill.description" placeholder="Description de la compétence..." class="skill-input skill-input-description" rows="2"></textarea>
              </div>
              <button @click="removeSkillRow(index)" class="btn-remove-skill" :disabled="localSkills.length === 1" title="Supprimer cette compétence">✕ Supprimer</button>
            </div>
            <button @click="addSkillRow" class="btn-add-skill">+ Ajouter une compétence</button>
          </div>
        </div>
      </div>
      <div id="sae_alignement">
        <p class="section_title">SAE concernée(s)* :</p>
        <div v-if="saeList.length === 0" class="no_sae_message">
          <p>Aucune SAE trouvée pour ce semestre</p>
        </div>
        <div v-else class="sae_switches_container">
          <div v-for="sae in saeList" :key="sae.idSAE" class="sae_switch_item">
            <label class="switch">
              <input type="checkbox" :checked="isSaeLinked(sae.idSAE)" @change="toggleSaeLink(sae.idSAE)">
              <span class="slider"></span>
            </label>
            <span class="sae_label" :title="`ID: ${sae.idSAE}, ApogeeCode: ${sae.apogeeCode}, Label: ${sae.label}`">{{  sae.label || 'SAE sans nom' }}</span>
          </div>
        </div>
      </div>
      <div id="align_items_inline_center">
        <div id="align_items_column_left">
          <p class="subsection_title">Mots clés</p>
          <div class="keywords-container">
            <div v-for="(keyword, index) in localKeywords" :key="index" class="keyword-item">
              <input type="text" v-model="keyword.keyword" @keydown.enter="handleKeywordEnter(index)" placeholder="Mot-clé..." class="keyword-input" />
              <button @click="removeKeyword(index)" class="btn-remove-keyword" title="Supprimer ce mot-clé">✕</button>
            </div>
            <button @click="addKeyword" class="btn-add-keyword">+ Ajouter un mot-clé</button>
          </div>
        </div>
        <div id="align_items_column_left">
          <p class="subsection_title">Modalités de mise en œuvre :</p>
          <div class="modalities-list">
            <div v-for="(modality, index) in localModalities" :key="index" class="modality-item">
              <textarea v-model="modality.modality" placeholder="Modalité de mise en œuvre..." class="modality-textarea" rows="3"></textarea>
              <button @click="removeModality(index)" class="btn-remove-modality" title="Supprimer cette modalité">✕</button>
            </div>
            <button @click="addModality" class="btn-add-modality">+ Ajouter une modalité</button>
          </div>
        </div>
      </div>
      <div>
        <p class="section_title">Répartition de heures ( volume étudiant ) : </p>
        <p>CM</p>
        <textarea class="auto-resize-textarea">1</textarea>
        <p>TD</p>
        <textarea class="auto-resize-textarea">1</textarea>
        <p>TP</p>
        <textarea class="auto-resize-textarea">1</textarea>
        <span>Le nombre total d'heure est .../...</span>
      </div>
      <div>
        <p class="section_title">Contenu pédagogique</p>
        <div class="pedagogical-content">
          <!-- CM Section -->
          <div class="pedagogical-section">
              <div class="pedagogical-header">
                <p class="pedagogical-title">CM</p>
              </div>
              <p class="pedagogical-subtitle">Détailler ici le contenu pédagogique des CM</p>

              <div class="pedagogical-items-container">
                <div v-if="localPedagogicalContent.cm.length === 0" class="no-content-message">
                  Aucun contenu CM
                </div>
                <div v-for="(item, index) in localPedagogicalContent.cm" :key="index" class="pedagogical-item">
                  <span class="pedagogical-number">{{ item.number }}</span>
                  <input type="text" v-model="item.content" placeholder="List item" class="pedagogical-input" />
                  <button @click="removePedagogicalItem('cm', index)" class="btn-remove-pedagogical" title="Supprimer">✕</button>
                </div>
              </div>

              <div class="pedagogical-footer">
                <button @click="addPedagogicalItem('cm')" class="btn-add-pedagogical">+ Ajouter</button>
              </div>
            </div>

            <!-- TD Section -->
            <div class="pedagogical-section">
              <div class="pedagogical-header">
                <p class="pedagogical-title">TD</p>
              </div>
              <p class="pedagogical-subtitle">Détailler ici le contenu pédagogique des TD</p>

              <div class="pedagogical-items-container">
                <div v-if="localPedagogicalContent.td.length === 0" class="no-content-message">
                  Aucun contenu TD
                </div>
                <div v-for="(item, index) in localPedagogicalContent.td" :key="index" class="pedagogical-item">
                  <span class="pedagogical-number">{{ item.number }}</span>
                  <input type="text" v-model="item.content" placeholder="List item" class="pedagogical-input" />
                  <button @click="removePedagogicalItem('td', index)" class="btn-remove-pedagogical" title="Supprimer">✕</button>
                </div>
              </div>

              <div class="pedagogical-footer">
                <button @click="addPedagogicalItem('td')" class="btn-add-pedagogical">+ Ajouter</button>
              </div>
            </div>

            <!-- TP Section -->
            <div class="pedagogical-section">
              <div class="pedagogical-header">
                <p class="pedagogical-title">TP</p>
              </div>
              <p class="pedagogical-subtitle">Détailler ici le contenu pédagogique des TP</p>

              <div class="pedagogical-items-container">
                <div v-if="localPedagogicalContent.tp.length === 0" class="no-content-message">
                  Aucun contenu TP
                </div>
                <div v-for="(item, index) in localPedagogicalContent.tp" :key="index" class="pedagogical-item">
                  <span class="pedagogical-number">{{ item.number }}</span>
                  <input type="text" v-model="item.content" placeholder="List item" class="pedagogical-input" />
                  <button @click="removePedagogicalItem('tp', index)" class="btn-remove-pedagogical" title="Supprimer">✕</button>
                </div>
              </div>

              <div class="pedagogical-footer">
                <button @click="addPedagogicalItem('tp')" class="btn-add-pedagogical">+ Ajouter</button>
              </div>
            </div>
          </div>
      </div>
      <div id="form">
        <p class="section_title">Suivi de la ressource / module</p>
        <div>
          <p>Retour de l'équipe pédagogique et des acteurs impactés</p>
          <textarea id="text_area_styled"></textarea>
          <p>Retour des étudiants</p>
          <textarea id="text_area_styled"></textarea>
          <p>Amélioration(s) à mettre en oeuvre</p>
          <textarea id="text_area_styled"></textarea>
        </div>
      </div>
      <div>
        <button>Modifier</button>
        <button>Sauvegarder</button>
      </div>
    </div>
  </div>
</template>

<style>

#Ressource_Sheet{
  margin: 3vw 14vw;
  justify-content: center;
}

.accordion {
  cursor: pointer;
  position: relative;
  padding-right: 2.5vw;
  border: none;
  outline: none;
  text-align: left;
  width: 100%;
  font-family: inherit;
  font-size: inherit;
}

.accordion::after {
  content: '^';
  position: absolute;
  right: 1vw;
  transition: transform 0.3s ease;
  font-size: 0.9vw;
}

.accordion.active::after {
  transform: rotate(180deg);
}

.panel {
  width: 90%;
  justify-self: center;
  padding: 0 18px;
  background-color: rgba(0,0,0,0.35);
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.2s ease-out, padding 0.2s ease-out;
  border-bottom-left-radius: 15px;
  border-bottom-right-radius: 15px;
  color: white;
  display: flex;
  flex-direction: column;
}

.panel p {
  margin-top: 0;
  padding-top: 1vw;
}

#return_Arrow{
  display: flex;
  align-items: center;
  cursor: pointer;
}

#return_Arrow > p{
  font-size: 1.5vw;
  font-weight: bold;
  color: black;
  margin-left: 1.5vw;
}

#back_arrow{
  font-size: 2vw;
  border: none;
  background-color: white;
  color: black;
  font-weight: bold;
}

#text_area_styled{
  width: 100%;
  min-height: 3em;
  border-radius: 15px;
  background-color: rgb(117, 117, 117);
  color: white;
  border: none;
  box-sizing: border-box;
  resize: none;
  padding: 1vw;
  overflow: hidden;
  overflow-wrap: break-word;
}

.auto-resize-textarea {
  overflow: hidden;
  resize: none;
  box-sizing: border-box;
  padding: 1vw;
  min-height: 3em;
  overflow-wrap: break-word;
}


#background_Form{
  height: auto;
  background-color: rgb(61, 67, 117);
  border-radius: 15px;
  overflow-x: hidden;
  overflow-y: auto;
  box-sizing: border-box;
  padding-bottom: 1vw;
  color: white;
}

.title{
  text-align: center;
  padding-top: 1vw;
  margin: 0;
  font-weight: lighter;
  font-size: 2.5vw;
}

#form{
  padding: 0 1vw;
  overflow: visible;
}

#dark_Bar{
  color: white;
  height: auto;
  border-radius: 10px;
  margin: 1vw 0 0 0;
  padding: 1vw;
  background-color: rgb(32,32,32);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* Header section styles*/
.header_Form{
  display: flex;
  gap: 1vw;
  padding: 1vw;
  justify-content: space-evenly;
}

.ref_Section {
  display: flex;
  gap: 0.5vw;
  padding: 0 2vw;
  margin-bottom: 1vw;
}

.ref_Section p {
  margin: 0;
}

/*main form styles*/
.section_title {
  font-size: 1.5vw;
  font-weight: bold;
  padding : 1vw 0 0.5vw 2vw;
}

.subsection_title {
  font-size: 1.2vw;
  font-weight: bold;
  padding : 0.5vw 0 0.5vw 2vw;
}

/*Toggle switch styles*/
.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 26px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.4s;
  border-radius: 26px;
}

.slider::before {
  position: absolute;
  content: "";
  height: 20px;
  width: 20px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.4s;
  border-radius: 50%;
}

/* ON state */
input:checked + .slider {
  background-color: #2C2C3B;
}

input:checked + .slider::before {
  transform: translateX(24px);
}

#sae_alignement{
  padding: 1vw 2vw;
}

.sae_switches_container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  gap: 2vw;
  margin-top: 1vw;
}

.sae_switch_item {
  display: flex;
  align-items: center;
  gap: 0.5vw;
}

.sae_label {
  font-size: 1.1vw;
  color: white;
}

.no_sae_message {
  padding: 0.5vw 0;
  font-style: italic;
  color: rgba(255, 255, 255, 0.7);
  text-align: center;
}

/* Skills table styles */
.skills-table {
  width: 100%;
  margin-top: 1vw;
}

.skills-header {
  display: grid;
  grid-template-columns: 2fr 4fr;
  gap: 1vw;
  padding: 0.5vw 0;
  font-weight: bold;
  border-bottom: 2px solid rgba(255, 255, 255, 0.3);
  margin-bottom: 1vw;
}

.skill-column-label,
.skill-column-description {
  padding: 0.5vw;
}

.skill-row {
  margin-bottom: 1.5vw;
}

.skill-inputs {
  display: grid;
  grid-template-columns: 2fr 4fr;
  gap: 1vw;
  margin-bottom: 0.5vw;
}

.skill-input {
  background-color: rgb(117, 117, 117);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  padding: 0.8vw;
  font-family: inherit;
  font-size: 1vw;
  width: 100%;
  box-sizing: border-box;
}

.skill-input:focus {
  outline: none;
  border-color: rgba(255, 255, 255, 0.5);
}

.skill-input-label {
  height: 2.5vw;
}

.skill-input-description {
  resize: vertical;
  min-height: 3vw;
}

.btn-remove-skill {
  background-color: transparent;
  color: #d9534f;
  border: 1px solid #d9534f;
  border-radius: 5px;
  padding: 0.4vw 1vw;
  cursor: pointer;
  font-size: 0.9vw;
  font-weight: bold;
  transition: all 0.3s;
  display: inline-block;
  width: auto;
  align-self: flex-start;
}

.btn-remove-skill:hover:not(:disabled) {
  background-color: #d9534f;
  color: white;
}

.btn-remove-skill:disabled {
  color: #6c757d;
  border-color: #6c757d;
  cursor: not-allowed;
  opacity: 0.5;
}

.btn-add-skill {
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.8vw 1.5vw;
  cursor: pointer;
  font-size: 1vw;
  font-weight: bold;
  margin-top: 1vw;
  transition: background-color 0.3s;
}

.btn-add-skill:hover {
  background-color: #2C2C3B;
}

#align_items_inline_center{
  display: flex;
  align-items: flex-start;
  justify-content: center;
  gap: 2vw;
  padding: 1vw 2vw;
}

#align_items_column_left{
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  gap: 1vw;
  flex: 1;
}

/* Keywords styles */
.keywords-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 0.8vw;
}

.keyword-item {
  position: relative;
  display: flex;
  align-items: center;
}

.keyword-input {
  flex: 1;
  background-color: rgb(117, 117, 117);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  padding: 0.6vw 3vw 0.6vw 1vw;
  font-family: inherit;
  font-size: 1vw;
  box-sizing: border-box;
}

.keyword-input:focus {
  outline: none;
  border-color: rgba(255, 255, 255, 0.5);
}

.btn-remove-keyword {
  position: absolute;
  right: 0.5vw;
  top: 50%;
  transform: translateY(-50%);
  background-color: transparent;
  color: white;
  border: none;
  width: 1.5vw;
  height: 1.5vw;
  cursor: pointer;
  font-size: 1.2vw;
  font-weight: bold;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  padding: 0;
}

.btn-remove-keyword:hover {
  color: #ff6b6b;
}


.btn-add-keyword {
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.6vw 1.2vw;
  cursor: pointer;
  font-size: 0.9vw;
  font-weight: bold;
  transition: background-color 0.3s;
  align-self: flex-start;
}

.btn-add-keyword:hover {
  background-color: #2C2C3B;
}

/* Modalities styles */
.modalities-list {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 1vw;
}

.modality-item {
  position: relative;
  display: flex;
  align-items: flex-start;
}

.modality-textarea {
  flex: 1;
  background-color: rgb(117, 117, 117);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  padding: 0.8vw 3vw 0.8vw 0.8vw;
  font-family: inherit;
  font-size: 1vw;
  resize: vertical;
  min-height: 4vw;
  box-sizing: border-box;
}

.modality-textarea:focus {
  outline: none;
  border-color: rgba(255, 255, 255, 0.5);
}

.btn-remove-modality {
  position: absolute;
  right: 0.5vw;
  top: 0.8vw;
  background-color: transparent;
  color: white;
  border: none;
  width: 1.5vw;
  height: 1.5vw;
  cursor: pointer;
  font-size: 1.2vw;
  font-weight: bold;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  padding: 0;
}

.btn-remove-modality:hover {
  color: #ff6b6b;
}


.btn-add-modality {
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.6vw 1.2vw;
  cursor: pointer;
  font-size: 0.9vw;
  font-weight: bold;
  transition: background-color 0.3s;
  align-self: flex-start;
}

.btn-add-modality:hover {
  background-color: #2C2C3B;
}

/* Pedagogical content styles */
.pedagogical-content {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5vw;
  margin: 1vw 0;
  padding: 0 2vw;
}

.pedagogical-section {
  background-color: rgba(117, 117, 117, 0.8);
  border-radius: 15px;
  padding: 1.5vw;
  display: flex;
  flex-direction: column;
  min-height: 25vw;
}

.pedagogical-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1vw;
}

.pedagogical-title {
  font-size: 1.3vw;
  font-weight: bold;
  margin: 0;
  color: white;
}

.pedagogical-subtitle {
  font-size: 0.75vw;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 1vw 0;
  line-height: 1.3;
}

.pedagogical-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 0.8vw;
  margin-bottom: 0.8vw;
  background-color: rgba(200, 200, 200, 0.3);
  border-radius: 8px;
  padding: 0.6vw 0.8vw;
}

.pedagogical-number {
  background-color: #2C2C3B;
  color: white;
  border-radius: 50%;
  width: 1.8vw;
  height: 1.8vw;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.9vw;
  flex-shrink: 0;
}

.pedagogical-input {
  flex: 1;
  background-color: transparent;
  color: white;
  border: none;
  padding: 0.4vw 2.5vw 0.4vw 0.5vw;
  font-family: inherit;
  font-size: 0.9vw;
  box-sizing: border-box;
}

.pedagogical-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.pedagogical-input:focus {
  outline: none;
}

.btn-remove-pedagogical {
  position: absolute;
  right: 0.5vw;
  top: 50%;
  transform: translateY(-50%);
  background-color: transparent;
  color: white;
  border: none;
  width: 1.5vw;
  height: 1.5vw;
  cursor: pointer;
  font-size: 1.1vw;
  font-weight: bold;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  padding: 0;
}

.btn-remove-pedagogical:hover {
  color: #ff6b6b;
}

.pedagogical-items-container {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 1vw;
}

.pedagogical-footer {
  display: flex;
  gap: 0.8vw;
  justify-content: flex-end;
  margin-top: auto;
  padding-top: 1vw;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.btn-add-pedagogical {
  background-color: rgba(108, 117, 125, 0.8);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.6vw 1.2vw;
  cursor: pointer;
  font-size: 0.85vw;
  font-weight: bold;
  transition: background-color 0.3s;
  width: 100%;
}

.btn-add-pedagogical:hover {
  background-color: rgba(108, 117, 125, 1);
}

.no-content-message {
  padding: 1vw;
  font-style: italic;
  color: rgba(255, 255, 255, 0.4);
  text-align: center;
  font-size: 0.85vw;
}
</style>
