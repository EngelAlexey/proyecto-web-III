package Interface

import Entity.Person

interface OnItemClickListener {
    fun onItemClicked (person: Person)
}