/**
 *@author Nikolaus Knop
 */

package memento.impl.adapters

import memento.MementoAdapter
import memento.field

internal class PairMementoAdapter(override val memorized: Pair<*, *>) : MementoAdapter<Pair<*, *>> {
    var first by field(memorized::first, memorized)
    var second by field(memorized::second, memorized)
}

internal class TripleMementoAdapter(override val memorized: Triple<*, *, *>): MementoAdapter<Triple<*, *, *>> {
    var first by field(memorized::first, memorized)
    var second by field(memorized::second, memorized)
    var third by field(memorized::third, memorized)
}