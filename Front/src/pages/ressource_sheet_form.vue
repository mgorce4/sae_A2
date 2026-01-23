<script setup>
import axios from 'axios'
import { status } from '../main'
import { onMounted, ref, nextTick, computed } from 'vue'

status.value = "Professeur"

/* Extract ID from hash URL parameters */
const getQueryParam = (param) => {
  const hash = window.location.hash
  const queryString = hash.split('?')[1]

  if (!queryString) {
    return null
  }

  const params = new URLSearchParams(queryString)
  return params.get(param)
}

const resourceSheetId = ref(getQueryParam('id'))

/* DTO API - Single data source */
const resourceSheetDTO = ref(null)

/* Local editable states */
const localObjectiveContent = ref('')
const localSkills = ref([])
const localKeywords = ref([])
const localModalities = ref([])
const localHours = ref({ cm: '', td: '', tp: '' })
const localHoursAlternance = ref({ cm: '', td: '', tp: '' })
const localSaeChanges = ref({})
const localPedagogicalContent = ref({
  cm: [],
  td: [],
  tp: [],
  ds: []
})
const localResourceTracking = ref({
  pedagogicalFeedback: '',
  studentFeedback: '',
  improvementSuggestions: ''
})

// SAE list - needs to be a ref to be populated after loading
const saeList = ref([])

// Validation errors for required fields
const validationErrors = ref({
  objective: false,
  skills: false,
  pedagogicalContent: false
})

// Computed properties from DTO
const ueLabels = computed(() => {
  if (!resourceSheetDTO.value?.ueReferences) return []
  return resourceSheetDTO.value.ueReferences.map(ue => ue.label)
})

const institutionName = computed(() => resourceSheetDTO.value?.department || '###')
const resourceName = computed(() => resourceSheetDTO.value?.resourceName || 'Nom de la ressource')
const resourceLabel = computed(() => resourceSheetDTO.value?.resourceLabel || '###')

const hoursPerStudent = computed(() => resourceSheetDTO.value?.hoursPN || null)
const hoursPerStudentAlternance = computed(() => resourceSheetDTO.value?.hoursPNAlternance || null)

const hasAlternanceHours = computed(() => {
  // Check if teacher alternance hours exist and have non-zero values
  const hasTeacherAlternance = resourceSheetDTO.value?.hoursTeacherAlternance != null &&
    (resourceSheetDTO.value.hoursTeacherAlternance.cm > 0 ||
     resourceSheetDTO.value.hoursTeacherAlternance.td > 0 ||
     resourceSheetDTO.value.hoursTeacherAlternance.tp > 0)

  // Check if PN alternance hours exist and have non-zero values
  const hasPNAlternance = resourceSheetDTO.value?.hoursPNAlternance != null &&
    (resourceSheetDTO.value.hoursPNAlternance.cm > 0 ||
     resourceSheetDTO.value.hoursPNAlternance.td > 0 ||
     resourceSheetDTO.value.hoursPNAlternance.tp > 0)

  // Check if local alternance hours have been entered
  const hasLocalAlternance = (localHoursAlternance.value.cm && localHoursAlternance.value.cm > 0) ||
                              (localHoursAlternance.value.td && localHoursAlternance.value.td > 0) ||
                              (localHoursAlternance.value.tp && localHoursAlternance.value.tp > 0)

  return hasTeacherAlternance || hasPNAlternance || hasLocalAlternance
})

const localHoursTotal = computed(() => {
  const cm = typeof localHours.value.cm === 'number' ? localHours.value.cm : (parseFloat(localHours.value.cm) || 0)
  const td = typeof localHours.value.td === 'number' ? localHours.value.td : (parseFloat(localHours.value.td) || 0)
  const tp = typeof localHours.value.tp === 'number' ? localHours.value.tp : (parseFloat(localHours.value.tp) || 0)
  return cm + td + tp
})

const localHoursAlternanceTotal = computed(() => {
  const cm = typeof localHoursAlternance.value.cm === 'number' ? localHoursAlternance.value.cm : (parseFloat(localHoursAlternance.value.cm) || 0)
  const td = typeof localHoursAlternance.value.td === 'number' ? localHoursAlternance.value.td : (parseFloat(localHoursAlternance.value.td) || 0)
  const tp = typeof localHoursAlternance.value.tp === 'number' ? localHoursAlternance.value.tp : (parseFloat(localHoursAlternance.value.tp) || 0)
  return cm + td + tp
})

const dbHoursTotal = computed(() => {
  if (!hoursPerStudent.value) return 0
  return (hoursPerStudent.value.cm || 0) + (hoursPerStudent.value.td || 0) + (hoursPerStudent.value.tp || 0)
})

const dbHoursTotalAlternance = computed(() => {
  if (!hoursPerStudentAlternance.value) return 0
  return (hoursPerStudentAlternance.value.cm || 0) + (hoursPerStudentAlternance.value.td || 0) + (hoursPerStudentAlternance.value.tp || 0)
})

// Function to check if SAE is linked
const isSaeLinked = (saeId) => {
  // Check if there's a local change
  if (saeId in localSaeChanges.value) {
    return localSaeChanges.value[saeId]
  }
  // Otherwise use the original value from DTO
  const sae = saeList.value.find(s => s.id === saeId)
  return sae?.isLinked || false
}

// Function to toggle SAE link
const toggleSaeLink = (saeId) => {
  const currentState = isSaeLinked(saeId)
  localSaeChanges.value[saeId] = !currentState
}

// Function to add a new pedagogical content item
const addPedagogicalItem = (type) => {
  const list = localPedagogicalContent.value[type]
  const nextNumber = list.length > 0 ? Math.max(...list.map(item => item.number)) + 1 : 1
  list.push({
    number: nextNumber,
    content: ''
  })
  // Update panel height after adding
  nextTick(() => {
    const pedagogicalPanel = document.querySelector('.pedagogical-content')?.closest('.panel')
    if (pedagogicalPanel) {
      updatePanelHeight(pedagogicalPanel)
    }
    // Initialize textarea heights
    initializeTextareaHeights()
  })
}

// Function to remove a pedagogical content item
const removePedagogicalItem = (type, index) => {
  localPedagogicalContent.value[type].splice(index, 1)

  // Recalculate numbers after removal
  localPedagogicalContent.value[type].forEach((item, idx) => {
    item.number = idx + 1
  })

  // Update panel height after removing
  nextTick(() => {
    const pedagogicalPanel = document.querySelector('.pedagogical-content')?.closest('.panel')
    if (pedagogicalPanel) {
      updatePanelHeight(pedagogicalPanel)
    }
    // Initialize textarea heights
    initializeTextareaHeights()
  })
}

// Drag and drop state for pedagogical items
const draggedItem = ref(null)
const draggedType = ref(null)

// Function to handle drag start
const onDragStart = (type, index) => {
  draggedItem.value = index
  draggedType.value = type
}

// Function to handle drag over
const onDragOver = (event) => {
  event.preventDefault()
}

// Function to handle drop
const onDrop = (type, dropIndex) => {
  if (draggedType.value === type && draggedItem.value !== null) {
    const items = localPedagogicalContent.value[type]
    const draggedElement = items[draggedItem.value]

    // Remove from old position
    items.splice(draggedItem.value, 1)

    // Insert at new position
    items.splice(dropIndex, 0, draggedElement)

    // Recalculate all numbers sequentially
    items.forEach((item, index) => {
      item.number = index + 1
    })

    // Reset drag state
    draggedItem.value = null
    draggedType.value = null

    // Update panel height
    nextTick(() => {
      const pedagogicalPanel = document.querySelector('.pedagogical-content')?.closest('.panel')
      if (pedagogicalPanel) {
        updatePanelHeight(pedagogicalPanel)
      }
      // Initialize textarea heights
      initializeTextareaHeights()
    })
  }
}

// Function to handle drag end
const onDragEnd = () => {
  draggedItem.value = null
  draggedType.value = null
}

// Function for return button
const goBack = () => {
  window.location.hash = '#/teacher-dashboard'
}

// Function to auto-resize textarea
const autoResizeTextarea = (event) => {
  const textarea = event.target
  // Reset height to auto to get the correct scrollHeight
  textarea.style.height = 'auto'
  // Set height to scrollHeight to fit all content
  textarea.style.height = textarea.scrollHeight + 'px'
}

// Function to initialize all textareas height on mount
const initializeTextareaHeights = () => {
  nextTick(() => {
    const textareas = document.querySelectorAll('.pedagogical-input')
    textareas.forEach(textarea => {
      textarea.style.height = 'auto'
      textarea.style.height = textarea.scrollHeight + 'px'
    })
  })
}

// Function to add a new keyword
const addKeyword = () => {
  localKeywords.value.push({ keyword: '', isNew: true })
}

// Function to remove a keyword
const removeKeyword = (index) => {
  localKeywords.value.splice(index, 1)
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
  // Update panel height after removing
  nextTick(() => {
    const modalitiesPanel = document.querySelector('.modalities-list')?.closest('.panel')
    if (modalitiesPanel) {
      updatePanelHeight(modalitiesPanel)
    }
  })
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
    // Update panel height after removing
    nextTick(() => {
      const skillsPanel = document.querySelector('.skills-table')?.closest('.panel')
      if (skillsPanel) {
        updatePanelHeight(skillsPanel)
      }
    })
  }
}

// Save function - sends data to backend
const saveResourceSheet = async () => {
  if (!resourceSheetId.value) {
    console.error('❌ No resource sheet ID')
    return
  }

  // Reset validation errors
  validationErrors.value = {
    objective: false,
    skills: false,
    pedagogicalContent: false
  }

  // Validate required fields
  let hasErrors = false

  // Check objective field
  if (!localObjectiveContent.value || localObjectiveContent.value.trim() === '') {
    validationErrors.value.objective = true
    hasErrors = true
  }

  // Check skills field - at least one skill with both label and description
  const validSkills = localSkills.value.filter(skill =>
    skill.label && skill.label.trim() !== '' &&
    skill.description && skill.description.trim() !== ''
  )

  if (validSkills.length === 0) {
    validationErrors.value.skills = true
    hasErrors = true
  }

  // Check pedagogical content - at least one item with content in at least one column
  console.log('🔍 Checking pedagogical content:', localPedagogicalContent.value)
  const hasValidPedagogicalContent =
    localPedagogicalContent.value.cm.some(item => item.content && item.content.trim() !== '') ||
    localPedagogicalContent.value.td.some(item => item.content && item.content.trim() !== '') ||
    localPedagogicalContent.value.tp.some(item => item.content && item.content.trim() !== '') ||
    localPedagogicalContent.value.ds.some(item => item.content && item.content.trim() !== '')

  console.log('✅ Has valid pedagogical content:', hasValidPedagogicalContent)

  if (!hasValidPedagogicalContent) {
    validationErrors.value.pedagogicalContent = true
    hasErrors = true
    console.log('❌ Pedagogical content validation failed')
  }

  // If there are validation errors, stop here and scroll to first error
  if (hasErrors) {
    // Scroll to first error
    nextTick(() => {
      if (validationErrors.value.objective) {
        const objectiveSection = document.querySelector('#form')
        if (objectiveSection) {
          objectiveSection.scrollIntoView({ behavior: 'smooth', block: 'start' })
        }
      } else if (validationErrors.value.skills) {
        const skillsSection = document.querySelectorAll('#form')[1]
        if (skillsSection) {
          skillsSection.scrollIntoView({ behavior: 'smooth', block: 'start' })
        }
      } else if (validationErrors.value.pedagogicalContent) {
        const pedagogicalSection = document.querySelector('#pedagogical_content_section')
        if (pedagogicalSection) {
          pedagogicalSection.scrollIntoView({ behavior: 'smooth', block: 'start' })
        }
      }
    })
    return
  }

  try {
    // Prepare the update DTO
    const updateDTO = {
      objective: localObjectiveContent.value,
      skills: localSkills.value.map(skill => ({
        id: skill.id,
        label: skill.label,
        description: skill.description
      })),
      keywords: localKeywords.value.map(kw => kw.keyword).filter(k => k && k.trim()),
      modalities: localModalities.value.map(m => m.modality).filter(m => m && m.trim()),
      linkedSaeIds: Object.entries(localSaeChanges.value)
        .filter(([, isLinked]) => isLinked)
        .map(([id]) => parseInt(id))
        .concat(
          // Add SAEs that were already linked and not changed
          saeList.value
            .filter(sae => sae.isLinked && !(sae.id in localSaeChanges.value))
            .map(sae => sae.id)
        ),
      teacherHours: {
        cm: String(localHours.value.cm || 0),
        td: String(localHours.value.td || 0),
        tp: String(localHours.value.tp || 0)
      },
      teacherHoursAlternance: hasAlternanceHours.value ? {
        cm: String(localHoursAlternance.value.cm || 0),
        td: String(localHoursAlternance.value.td || 0),
        tp: String(localHoursAlternance.value.tp || 0)
      } : null,
      pedagogicalContent: {
        cm: localPedagogicalContent.value.cm.map(item => ({ order: item.number, content: item.content })),
        td: localPedagogicalContent.value.td.map(item => ({ order: item.number, content: item.content })),
        tp: localPedagogicalContent.value.tp.map(item => ({ order: item.number, content: item.content })),
        ds: localPedagogicalContent.value.ds.map(item => ({ order: item.number, content: item.content }))
      },
      tracking: {
        pedagogicalFeedback: localResourceTracking.value.pedagogicalFeedback,
        studentFeedback: localResourceTracking.value.studentFeedback,
        improvementSuggestions: localResourceTracking.value.improvementSuggestions
      }
    }

    console.log('📤 Sending update DTO:', updateDTO)
    console.log('📊 Teacher hours:', updateDTO.teacherHours)
    console.log('📊 Teacher hours alternance:', updateDTO.teacherHoursAlternance)

    // Send PUT request
    await axios.put(
      `http://localhost:8080/api/v2/resource-sheets/${resourceSheetId.value}`,
      updateDTO
    )


    // Reload the data to show updated values
    location.reload()

  } catch (error) {
    console.error('❌ Error saving resource sheet:', error)
    console.error('Error details:', error.response?.data || error.message)
  }
}

onMounted(async () => {
  /* Fetch complete resource sheet using DTO API - ONE REQUEST FOR ALL DATA! */
  if (resourceSheetId.value) {
    try {
      const response = await axios.get(`http://localhost:8080/api/v2/resource-sheets/${resourceSheetId.value}`)
      resourceSheetDTO.value = response.data

      // Vérifier immédiatement si le DTO contient des données
      if (!resourceSheetDTO.value) {
        console.error('❌ No data in resource sheet DTO')
        return
      }

      // Initialize local states from DTO

      // Objective
      localObjectiveContent.value = resourceSheetDTO.value.objective || ''

      // Skills
      if (resourceSheetDTO.value.skills && resourceSheetDTO.value.skills.length > 0) {
        localSkills.value = resourceSheetDTO.value.skills.map(skill => ({
          id: skill.id,
          label: skill.label || '',
          description: skill.description || ''
        }))
      } else {
        localSkills.value = [{ id: null, label: '', description: '' }]
      }

      // Keywords
      if (resourceSheetDTO.value.keywords && resourceSheetDTO.value.keywords.length > 0) {
        localKeywords.value = resourceSheetDTO.value.keywords.map(kw => ({ keyword: kw, isNew: false }))
      } else {
        localKeywords.value = []
      }

      // Modalities
      if (resourceSheetDTO.value.modalities && resourceSheetDTO.value.modalities.length > 0) {
        localModalities.value = resourceSheetDTO.value.modalities.map(mod => ({ modality: mod, isNew: false }))
      } else {
        localModalities.value = []
      }

      // Hours - si hoursTeacher existe, on affiche ses valeurs, sinon on laisse vide (hoursPN sera en placeholder)
      console.log('📊 hoursTeacher from DTO:', resourceSheetDTO.value.hoursTeacher)
      console.log('📊 hoursPN from DTO:', resourceSheetDTO.value.hoursPN)

      if (resourceSheetDTO.value.hoursTeacher) {
        // Teacher hours exist - use them (convert null or undefined to empty string for display)
        localHours.value = {
          cm: resourceSheetDTO.value.hoursTeacher.cm !== null && resourceSheetDTO.value.hoursTeacher.cm !== undefined ? resourceSheetDTO.value.hoursTeacher.cm : '',
          td: resourceSheetDTO.value.hoursTeacher.td !== null && resourceSheetDTO.value.hoursTeacher.td !== undefined ? resourceSheetDTO.value.hoursTeacher.td : '',
          tp: resourceSheetDTO.value.hoursTeacher.tp !== null && resourceSheetDTO.value.hoursTeacher.tp !== undefined ? resourceSheetDTO.value.hoursTeacher.tp : ''
        }
        console.log('✅ Using hoursTeacher:', localHours.value)
      } else {
        // No teacher hours - leave empty (will show PN hours in placeholder)
        localHours.value = { cm: '', td: '', tp: '' }
        console.log('📝 No hoursTeacher, fields will be empty with hoursPN as placeholder')
      }

      // Hours Alternance
      console.log('📊 hoursTeacherAlternance from DTO:', resourceSheetDTO.value.hoursTeacherAlternance)
      console.log('📊 hoursPNAlternance from DTO:', resourceSheetDTO.value.hoursPNAlternance)

      if (resourceSheetDTO.value.hoursTeacherAlternance) {
        // Teacher hours alternance exist - use them
        localHoursAlternance.value = {
          cm: resourceSheetDTO.value.hoursTeacherAlternance.cm !== null && resourceSheetDTO.value.hoursTeacherAlternance.cm !== undefined ? resourceSheetDTO.value.hoursTeacherAlternance.cm : '',
          td: resourceSheetDTO.value.hoursTeacherAlternance.td !== null && resourceSheetDTO.value.hoursTeacherAlternance.td !== undefined ? resourceSheetDTO.value.hoursTeacherAlternance.td : '',
          tp: resourceSheetDTO.value.hoursTeacherAlternance.tp !== null && resourceSheetDTO.value.hoursTeacherAlternance.tp !== undefined ? resourceSheetDTO.value.hoursTeacherAlternance.tp : ''
        }
        console.log('✅ Using hoursTeacherAlternance:', localHoursAlternance.value)
      } else {
        // No teacher hours alternance
        localHoursAlternance.value = { cm: '', td: '', tp: '' }
        console.log('📝 No hoursTeacherAlternance, fields will be empty')
      }

      // Pedagogical content
      if (resourceSheetDTO.value.pedagogicalContent) {
        const content = resourceSheetDTO.value.pedagogicalContent
        // Convert DTO format (order, content) to local format (number, content)
        localPedagogicalContent.value = {
          cm: (content.cm || []).map(item => ({ number: item.order, content: item.content })),
          td: (content.td || []).map(item => ({ number: item.order, content: item.content })),
          tp: (content.tp || []).map(item => ({ number: item.order, content: item.content })),
          ds: (content.ds || []).map(item => ({ number: item.order, content: item.content }))
        }
      } else {
        localPedagogicalContent.value = { cm: [], td: [], tp: [], ds: [] }
      }

      // Resource tracking
      if (resourceSheetDTO.value.tracking) {
        localResourceTracking.value = {
          pedagogicalFeedback: resourceSheetDTO.value.tracking.pedagogicalFeedback || '',
          studentFeedback: resourceSheetDTO.value.tracking.studentFeedback || '',
          improvementSuggestions: resourceSheetDTO.value.tracking.improvementSuggestions || ''
        }
      }

      // SAEs list - Use linkedSaes directly from ResourceSheet DTO
      // The DTO already contains ALL SAEs from the same semester with isLinked flag correctly set
      if (resourceSheetDTO.value.linkedSaes && resourceSheetDTO.value.linkedSaes.length > 0) {
        // Use the SAEs from ResourceSheet DTO directly - they already have the correct isLinked flag
        saeList.value = resourceSheetDTO.value.linkedSaes.map(sae => ({
          id: sae.id,
          label: sae.label,
          apogeeCode: sae.apogeeCode,
          isLinked: sae.isLinked  // Use the isLinked value from backend - it's already correct!
        }))

      } else {
        saeList.value = []
      }

    } catch (error) {
      console.error('❌ Error loading resource sheet:', error)
      console.error('Error details:', error.response?.data || error.message)
    }
  }

  // Wait for DOM to be fully rendered
  await nextTick()

  // Initialize accordion after DOM is ready
  const acc = document.getElementsByClassName("accordion")

  for (let i = 0; i < acc.length; i++) {
    acc[i].addEventListener("click", function() {
      this.classList.toggle("active")
      const panel = this.nextElementSibling
      if (panel.style.maxHeight) {
        panel.style.maxHeight = null
        panel.style.padding = "0 18px"
      } else {
        panel.style.padding = "2vw 18px"
        panel.style.maxHeight = panel.scrollHeight + "px"
      }
    })
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

  // Initialize all textarea heights for pedagogical content
  initializeTextareaHeights();
})

const show_popup_pedago = ref(false)

function toggleShowPopUpPedago() {
    show_popup_pedago.value = !show_popup_pedago.value
}

const show_popup_sae = ref(false)

function toggleShowPopUpSae() {
    show_popup_sae.value = !show_popup_sae.value
}

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
        <p class="title">{{ resourceName }}</p>
        <p>Dep : </p>
        <p>{{ institutionName }}</p>
      </div>
      <div class="ref_Section">
        <p>Réf. ressource : </p>
        <p>{{ resourceLabel }}</p>
      </div>
      <div id="form">
        <button class="accordion" id="dark_Bar">Objectif de la ressource *</button>
        <div class="panel">
          <textarea id="text_area_styled" v-model="localObjectiveContent" placeholder="Saisissez les objectifs de la ressource..."></textarea>
          <span v-if="validationErrors.objective" class="error-message">Merci de remplir ce champ</span>
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
          <span v-if="validationErrors.skills" class="error-message">Merci de remplir au moins une compétence avec un label et une description</span>
        </div>
      </div>
      <div id="sae_alignement">
          <div style="display: flex; align-items: center">
              <p class="section_title">SAE(s) concérnée(s) *</p>
              <p v-if="status" class="btn_how_to" @click="toggleShowPopUpSae">ⓘ</p>
              <div v-show="show_popup_sae" id="popup_sae">
                  Cliquer sur l'interrupteur pour lier une SAE à cette ressource
              </div>
        </div>
        <div v-if="saeList.length === 0" class="no_sae_message">
          <p>Aucune SAE trouvée pour ce semestre</p>
        </div>
        <div v-else class="sae_switches_container">
          <div v-for="sae in saeList" :key="sae.id" class="sae_switch_item">
            <label class="switch">
              <input type="checkbox" :checked="isSaeLinked(sae.id)" @change="toggleSaeLink(sae.id)">
              <span class="slider"></span>
            </label>
            <span class="sae_label" :title="`ID: ${sae.id}, ApogeeCode: ${sae.apogeeCode}, Label: ${sae.label}`">{{  sae.label || 'SAE sans nom' }}</span>
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
      <div id="hours_section">
        <p class="section_title">Répartition des heures (volume étudiant) * :</p>

        <!-- Formation Initiale Hours -->
        <p class="subsection_title" style="color: white; margin-top: 1.5vw; margin-bottom: 0.5vw;">Formation Initiale</p>
        <div class="hours_container">
          <div class="hours_row">
            <div class="hours_item">
              <label class="hours_label">CM</label>
              <div class="hours_box">
                <input
                  type="number"
                  v-model.number="localHours.cm"
                  class="hours_display"
                  min="0"
                  step="0.5"
                  :placeholder="hoursPerStudent?.cm || 0"
                />
              </div>
            </div>
            <div class="hours_item">
              <label class="hours_label">TD</label>
              <div class="hours_box">
                <input
                  type="number"
                  v-model.number="localHours.td"
                  class="hours_display"
                  min="0"
                  step="0.5"
                  :placeholder="hoursPerStudent?.td || 0"
                />
              </div>
            </div>
            <div class="hours_item">
              <label class="hours_label">TP</label>
              <div class="hours_box">
                <input
                  type="number"
                  v-model.number="localHours.tp"
                  class="hours_display"
                  min="0"
                  step="0.5"
                  :placeholder="hoursPerStudent?.tp || 0"
                />
              </div>
            </div>
            <div class="hours_total_display">
              <p class="hours_total_text">Nombre d'heures total : <span class="hours_total_value">{{ localHoursTotal }}</span> / <span class="hours_total_value">{{ dbHoursTotal }}</span></p>
            </div>
          </div>
        </div>

        <!-- Alternance Hours (only if exists or has data) -->
        <template v-if="hasAlternanceHours">
          <p class="subsection_title" style="color: white; margin-top: 1.5vw; margin-bottom: 0.5vw;">Alternance</p>
          <div class="hours_container">
            <div class="hours_row">
              <div class="hours_item">
                <label class="hours_label">CM</label>
                <div class="hours_box">
                  <input
                    type="number"
                    v-model.number="localHoursAlternance.cm"
                    class="hours_display"
                    min="0"
                    step="0.5"
                    :placeholder="hoursPerStudentAlternance?.cm || 0"
                  />
                </div>
              </div>
              <div class="hours_item">
                <label class="hours_label">TD</label>
                <div class="hours_box">
                  <input
                    type="number"
                    v-model.number="localHoursAlternance.td"
                    class="hours_display"
                    min="0"
                    step="0.5"
                    :placeholder="hoursPerStudentAlternance?.td || 0"
                  />
                </div>
              </div>
              <div class="hours_item">
                <label class="hours_label">TP</label>
                <div class="hours_box">
                  <input
                    type="number"
                    v-model.number="localHoursAlternance.tp"
                    class="hours_display"
                    min="0"
                    step="0.5"
                    :placeholder="hoursPerStudentAlternance?.tp || 0"
                  />
                </div>
              </div>
              <div class="hours_total_display">
                <p class="hours_total_text">Nombre d'heures total : <span class="hours_total_value">{{ localHoursAlternanceTotal }}</span> / <span class="hours_total_value">{{ dbHoursTotalAlternance }}</span></p>
              </div>
            </div>
          </div>
        </template>
      </div>
      <div id="pedagogical_content_section">

          <div style="display: flex; align-items: center">
              <p class="section_title">Contenu pédagogique *</p>
              <p v-if="status" class="btn_how_to" @click="toggleShowPopUpPedago">ⓘ</p>
              <div v-show="show_popup_pedago" id="popup_pedago">
                  Ici, vous devez décrire les séances éffectuées
              </div>
          </div>
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
                <div v-for="(item, index) in localPedagogicalContent.cm" :key="index" class="pedagogical-item" draggable="true" @dragstart="onDragStart('cm', index)" @dragover="onDragOver" @drop="onDrop('cm', index)" @dragend="onDragEnd">
                  <span class="pedagogical-number">{{ item.number }}</span>
                  <textarea v-model="item.content" placeholder="Contenu..." class="pedagogical-input" rows="1" @input="autoResizeTextarea"></textarea>
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
                <div v-for="(item, index) in localPedagogicalContent.td" :key="index" class="pedagogical-item" draggable="true" @dragstart="onDragStart('td', index)" @dragover="onDragOver" @drop="onDrop('td', index)" @dragend="onDragEnd">
                  <span class="pedagogical-number">{{ item.number }}</span>
                  <textarea v-model="item.content" placeholder="Contenu..." class="pedagogical-input" rows="1" @input="autoResizeTextarea"></textarea>
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
                <div v-for="(item, index) in localPedagogicalContent.tp" :key="index" class="pedagogical-item" draggable="true" @dragstart="onDragStart('tp', index)" @dragover="onDragOver" @drop="onDrop('tp', index)" @dragend="onDragEnd">
                  <span class="pedagogical-number">{{ item.number }}</span>
                  <textarea v-model="item.content" placeholder="Contenu..." class="pedagogical-input" rows="1" @input="autoResizeTextarea"></textarea>
                  <button @click="removePedagogicalItem('tp', index)" class="btn-remove-pedagogical" title="Supprimer">✕</button>
                </div>
              </div>
              <div class="pedagogical-footer">
                <button @click="addPedagogicalItem('tp')" class="btn-add-pedagogical">+ Ajouter</button>
              </div>
            </div>

            <!-- DS Section -->
            <div class="pedagogical-section">
              <div class="pedagogical-header">
                <p class="pedagogical-title">DS</p>
              </div>
              <p class="pedagogical-subtitle">Détailler ici le contenu pédagogique des DS</p>
              <div class="pedagogical-items-container">
                <div v-if="localPedagogicalContent.ds.length === 0" class="no-content-message">
                  Aucun contenu DS
                </div>
                <div v-for="(item, index) in localPedagogicalContent.ds" :key="index" class="pedagogical-item" draggable="true" @dragstart="onDragStart('ds', index)" @dragover="onDragOver" @drop="onDrop('ds', index)" @dragend="onDragEnd">
                  <span class="pedagogical-number">{{ item.number }}</span>
                  <textarea v-model="item.content" placeholder="Contenu..." class="pedagogical-input" rows="1" @input="autoResizeTextarea"></textarea>
                  <button @click="removePedagogicalItem('ds', index)" class="btn-remove-pedagogical" title="Supprimer">✕</button>
                </div>
              </div>

              <div class="pedagogical-footer">
                <button @click="addPedagogicalItem('ds')" class="btn-add-pedagogical">+ Ajouter</button>
              </div>
            </div>
          </div>
          <span v-if="validationErrors.pedagogicalContent" class="error-message">Merci de remplir au moins un contenu dans une des colonnes (CM, TD, TP ou DS)</span>
      </div>
      <div id="form">
        <p class="section_title">Suivi de la ressource / module</p>
        <div>
          <p>Retour de l'équipe pédagogique et des acteurs impactés</p>
          <textarea
            v-model="localResourceTracking.pedagogicalFeedback"
            id="text_area_styled"
            class="tracking-textarea"
            placeholder="Saisir le retour de l'équipe pédagogique..."
          ></textarea>
          <p>Retour des étudiants</p>
          <textarea
            v-model="localResourceTracking.studentFeedback"
            id="text_area_styled"
            class="tracking-textarea"
            placeholder="Saisir le retour des étudiants..."
          ></textarea>
          <p>Amélioration(s) à mettre en oeuvre</p>
          <textarea
            v-model="localResourceTracking.improvementSuggestions"
            id="text_area_styled"
            class="tracking-textarea"
            placeholder="Saisir les améliorations à mettre en œuvre..."
          ></textarea>
        </div>
      </div>

      <!-- Save button container -->
      <div class="save-button-container">
        <button id="button_save" @click="saveResourceSheet">Sauvegarder</button>
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
  color: var(--main-theme-secondary-color);
  display: flex;
  flex-direction: column;
}

.panel p {
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
  color: var(--main-theme-tertiary-color);
  margin-left: 1.5vw;
}

#back_arrow{
  font-size: 2vw;
  border: none;
  background-color: var(--main-theme-secondary-color);
  color: var(--main-theme-tertiary-color);
  font-weight: bold;
}

#text_area_styled{
  width: 100%;
  min-height: 3em;
  border-radius: 15px;
  background-color: var(--div-rect-background-color);
  color: var(--main-theme-secondary-color);
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
  background-color: var(--main-theme-background-color);
  border-radius: 15px;
  overflow-x: hidden;
  overflow-y: auto;
  box-sizing: border-box;
  padding-bottom: 1vw;
  color: var(--main-theme-secondary-color);
}

.title{
  text-align: center;
  padding-top: 1vw;
  font-weight: lighter;
  font-size: 2.5vw;
}

#form{
  padding: 0 1vw;
  overflow: visible;
}

#dark_Bar{
  color: var(--main-theme-secondary-color);
  height: auto;
  border-radius: 10px;
  margin: 1vw 0 0 0;
  padding: 1vw;
  background-color: var(--clickable-background-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* Header section styles*/
.header_Form{
  display: flex;
  gap: 1vw;
  padding: 1vw;
  justify-content: space-between;
  align-items: center;
}

.header_Form > p:first-child,
.header_Form > p:nth-child(2) {
  flex-shrink: 0;
  white-space: nowrap;
}

.header_Form > p.title {
  flex: 1;
  text-align: center;
  word-wrap: break-word;
  overflow-wrap: break-word;
  white-space: normal;
  max-width: 50%;
  margin: auto;
}

.header_Form > p:nth-child(4),
.header_Form > p:nth-child(5) {
  flex-shrink: 0;
  white-space: nowrap;
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

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--sub-div-background-color);
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
  background-color: var(--main-theme-secondary-color);
  transition: 0.4s;
  border-radius: 50%;
}

/* ON state */
input:checked + .slider {
  background-color: var(--clickable-background-color);
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
  color: var(--main-theme-secondary-color);
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
  background-color: var(--div-rect-background-color);
  color: var(--main-theme-secondary-color);
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
  color: var(--error-color);
  border: 1px solid var(--error-color);
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
  background-color: var(--error-color);
  color: var(--error-color);
}

.btn-remove-skill:disabled {
  color: var(--div-rect-background-color);
  border-color: var(--div-rect-background-color);
  cursor: not-allowed;
  opacity: 0.5;
}

.btn-add-skill {
  background-color: var(--div-rect-background-color);
  color: var(--main-theme-secondary-color);
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
  background-color: var(--sub-section-background-color);
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
  background-color: var(--div-rect-background-color);
  color: var(--main-theme-secondary-color);
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
  color: var(--main-theme-secondary-color);
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
}

.btn-remove-keyword:hover {
  color: var(--error-color);
}


.btn-add-keyword {
  background-color: var(--div-rect-background-color);
  color: var(--main-theme-secondary-color);
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
  background-color: var(--sub-section-background-color);
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
  background-color: var(--div-rect-background-color);
  color: var(--main-theme-secondary-color);
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
  color: var(--main-theme-secondary-color);
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
}

.btn-remove-modality:hover {
  color: var(--error-color);
}


.btn-add-modality {
  background-color: var(--div-rect-background-color);
  color: var(--main-theme-secondary-color);
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
  background-color: var(--sub-section-background-color);
}

/* Pedagogical content styles */
#pedagogical_content_section {
  padding: 0 1vw;
  margin-top: 1.5vw;
}

.pedagogical-content {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.2vw;
  margin: 1vw 0;
  padding: 0 2vw;
  box-sizing: border-box;
  overflow: visible;
  align-items: start;
}

.pedagogical-section {
  background-color: rgba(117, 117, 117, 0.8);
  border-radius: 15px;
  padding: 1.5vw;
  display: flex;
  flex-direction: column;
  max-width: 100%;
  box-sizing: border-box;
  overflow: hidden;
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
  color: var(--main-theme-secondary-color);
}

.pedagogical-subtitle {
  font-size: 0.75vw;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 1vw 0;
  line-height: 1.3;
}

.pedagogical-items-container {
  flex: 0 1 auto;
  overflow-y: visible;
  margin-bottom: 1vw;
  min-height: 5vw;
}

.pedagogical-item {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: 0.8vw;
  margin-bottom: 0.8vw;
  background-color: rgba(200, 200, 200, 0.3);
  border-radius: 8px;
  padding: 0.6vw 0.8vw;
  cursor: move;
  transition: all 0.2s ease;
  min-height: 2.5vw;
  box-sizing: border-box;
  width: 100%;
  overflow: visible;
}

.pedagogical-item:hover {
  background-color: rgba(200, 200, 200, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.pedagogical-item[draggable="true"]:active {
  cursor: grabbing;
}


.pedagogical-item.dragging {
  opacity: 0.5;
  transform: scale(0.95);
}

.no-content-message {
  color: rgba(255, 255, 255, 0.5);
  font-style: italic;
  text-align: center;
  padding: 2vw 0;
  font-size: 0.9vw;
}

.pedagogical-number {
  background-color: var(--sub-section-background-color);
  color: var(--main-theme-secondary-color);
  border-radius: 50%;
  width: 1.8vw;
  height: 1.8vw;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.9vw;
}

.pedagogical-input {
  flex: 1;
  background-color: transparent;
  color: var(--main-theme-secondary-color);
  border: none;
  padding: 0.4vw 2.5vw 0.4vw 0.5vw;
  font-family: inherit;
  font-size: 0.9vw;
  box-sizing: border-box;
  resize: none;
  overflow: hidden;
  min-height: 1.5vw;
  line-height: 1.4;
  width: 100%;
  word-wrap: break-word;
  word-break: break-word;
  overflow-wrap: break-word;
  white-space: pre-wrap;
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
  top: 0.6vw;
  background-color: transparent;
  color: var(--main-theme-secondary-color);
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
}

.btn-remove-pedagogical:hover {
  color: var(--error-color);
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
  color: var(--main-theme-secondary-color);
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

/* Hours Section Styles */
#hours_section {
  padding: 2vw 1vw;
  margin-top: 1.5vw;
  background-color: var(--main-theme-background-color);
  border-radius: 15px;
}

.hours_container {
  width: 100%;
  display: flex;
  justify-content: center;
}

.hours_row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2.5vw;
  margin: 1.5vw 0;
  flex-wrap: wrap;
}

.hours_item {
  display: flex;
  flex-direction: column;
  gap: 0.8vw;
  align-items: center;
}

.hours_label {
  font-weight: bold;
  font-size: 1.1vw;
  color: var(--main-theme-secondary-color);
  text-transform: uppercase;
  letter-spacing: 0.1vw;
}

.hours_box {
  background-color: rgba(117, 117, 117, 0.8);
  border-radius: 12px;
  border: 2px solid rgba(0, 0, 0, 0.2);
  padding: 1vw 1.2vw;
  min-width: 4.5vw;
  max-width: 5vw;
  min-height: 3.5vw;
  max-height: 4vw;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.4);
}

.hours_display {
  background-color: transparent;
  color: var(--main-theme-secondary-color);
  border: none;
  font-size: 2.2vw;
  font-weight: bold;
  text-align: center;
  width: 100%;
  outline: none;
}

.hours_display::placeholder {
  color: rgba(255, 255, 255, 0.4);
  opacity: 1;
  font-weight: bold;
}

.hours_display::-webkit-inner-spin-button,
.hours_display::-webkit-outer-spin-button {
  -webkit-appearance: none;
}

.hours_display[type="number"] {
  -moz-appearance: textfield;
  appearance: textfield;
}

.hours_total_display {
  display: flex;
  align-items: center;
  padding: 0.8vw 1.5vw;
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 10px;
  margin-left: 2vw;
}

.hours_total_text {
  font-size: 1.1vw;
  color: var(--main-theme-secondary-color);
  white-space: nowrap;
}

.hours_total_value {
  font-weight: bold;
  color: var(--main-theme-secondary-color);
  font-size: 1.3vw;
}

/* Validation error messages */
.error-message {
  color: var(--error-color);
  font-size: 0.9vw;
  margin-top: 0.5vw;
  display: block;
  font-weight: normal;
  padding-left: 2vw;
}

/* Save button container - centers the button with spacing */
.save-button-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 3vw;
  margin-bottom: 3vw;
  padding: 2vw 0;
}

#button_save {
  background-color: var(--sub-section-background-color);
  color: var(--main-theme-secondary-color);
  font-size: 1.2vw;
  font-weight: 500;
  padding: 1.2vw 4vw;
  border: none;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

#button_save:hover {
  background-color: var(--div-rect-background-color);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
}

#button_save:active {
  background-color: var(--sub-section-background-color);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
}

#popup_pedago {
    z-index: 10;
    color: white;
    background-color: var(--sub-div-background-color);
    border-radius: 15px;
    padding: 0.6vw;
    font-size: 0.8vw;
    max-width: 14.5vw;
    text-align: justify;
}

#popup_pedago::after {
    content: "";
    position: absolute;
    top: 90.35vw;
    right: 60.89vw;
    rotate: 90deg;
    border-left: 0.8vw solid transparent;
    border-right: 0.8vw solid transparent;
    border-top: 0.8vw solid var(--sub-div-background-color);
}

#popup_sae {
    z-index: 10;
    color: white;
    background-color: var(--sub-div-background-color);
    border-radius: 15px;
    padding: 0.6vw;
    font-size: 0.8vw;
    max-width: 14.5vw;
    text-align: justify;
}

#popup_sae::after {
    content: "";
    position: absolute;
    top: 35.35vw;
    right: 61vw;
    rotate: 90deg;
    border-left: 0.8vw solid transparent;
    border-right: 0.8vw solid transparent;
    border-top: 0.8vw solid var(--sub-div-background-color);
}
</style>
