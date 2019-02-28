import java.security.MessageDigest
import java.time.LocalDateTime

fun main() {
    with (SimpleBlockchain(3)) {
        generateGenesisBlock()
        addNewBlock("Hello World")
        addNewBlock("Give me some beer")
        addNewBlock("Ajax Amsterdam")
        addNewBlock("Love programming")

        printToConsole()
    }

    with (SimpleBlockchain()) {
        generateGenesisBlock()
        addNewBlock("Hello World")
        addNewBlock("Give me some beer")
        addNewBlock("Ajax Amsterdam")
        addNewBlock("Love programming")

        printToConsole()
    }
}


class SimpleBlockchain(val complexity : Int = 5) {
    private val blockchain = ArrayList<Block>()

    fun generateGenesisBlock() {
        val data = "Genesis block"
        val timestamp = LocalDateTime.now()
        val newBlockHash = generateHash(data, "", timestamp, 0)
        blockchain.add(Block(newBlockHash, "genesis",  data, "", timestamp))
    }

    fun addNewBlock(data : String) {
        val newBlock = mineBlock(data)
        blockchain.add(newBlock)
    }

    private fun mineBlock(data : String) : Block {
        val timestamp = LocalDateTime.now()
        val previousBlockHash = blockchain.last().hash
        for (nonce in 0 until Integer.MAX_VALUE) {
            val hash = generateHash(data, previousBlockHash, timestamp, nonce)
            val complexityMatchRegex = "^0{$complexity}".toRegex()
            if (hash.substring(0, complexity).matches(complexityMatchRegex)) {
                return Block(hash, nonce.toString(), data, previousBlockHash, timestamp)

            }
        }
        throw RuntimeException("Failed to mine block")
    }

    fun printToConsole() {
        println("\n## Blockchain with mining complexity of $complexity")
        println("-----------------------------------------\n")
        for ((index, block) in blockchain.withIndex()) {
            println("Block $index: $block")
        }
        println("\n-----------------------------------------\n")
    }
}

data class Block(val hash : String, val nonce : String, val data : String, val previousBlockHash : String, val timestamp : LocalDateTime)

// Some util functions

fun generateHash(data : String, previousBlockHash : String, timestamp : LocalDateTime, nonce : Int) : String {
    return applySha256(nonce.toString() + data + previousBlockHash + timestamp.toString())
}

fun applySha256(input: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(input.toByteArray(charset("UTF-8")))
    val hexString = StringBuffer()
    for (i in hash.indices) {
        val hex = Integer.toHexString(0xff and hash[i].toInt())
        if (hex.length == 1) hexString.append('0')
        hexString.append(hex)
    }
    return hexString.toString()
}