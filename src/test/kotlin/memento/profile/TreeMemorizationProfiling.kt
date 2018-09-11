/**
 * @author Nikolaus Knop
 */

package memento.profile

import memento.Memorizer
import nikok.kprofile.api.profile

fun profileTreeMemorization() = profile("tree memorization", currentTags) {
    withMemorizer(Memorizer.newInstance()) {

    }
}