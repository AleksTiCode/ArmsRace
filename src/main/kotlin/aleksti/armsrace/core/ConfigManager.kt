package aleksti.armsrace.core

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.neoforged.fml.loading.FMLPaths
import java.io.File

object ConfigManager {

    // 1. Настраиваем "Переводчик" JSON
    private val jsonFormat = Json {
        prettyPrint = true // Делает JSON красивым (с переносами строк и отступами), чтобы админу было удобно читать
        ignoreUnknownKeys = true // Если админ напишет в конфиге отсебятину, мод не крашнется, а просто проигнорирует её
    }

    // 2. Указываем путь к файлу: папка_сервера/config/armsrace_arenas.json
    private val configFile: File = FMLPaths.CONFIGDIR.get().resolve("armsrace_arenas.json").toFile()

    // Здесь мы будем хранить загруженные арены в оперативной памяти
    var templates = listOf<LobbyTemplate>()

    // 3. Главная функция. Её нужно будет вызвать ОДИН РАЗ в FMLCommonSetupEvent
    fun loadConfigs() {
        try {
            // ПРОВЕРКА: Существует ли файл на жестком диске?
            if (!configFile.exists()) {

                // --- РЕЖИМ СОЗДАТЕЛЯ (Файла нет) ---
                println("Конфиг не найден. Создаю базовый шаблон...")

                // Создаем болванку для примера
                val defaultTemplate = LobbyTemplate(
                    templateId = "vanilla",
                    teams = listOf(TeamTemplate("1", "§b",listOf(SpawnPoint(143.0, -57.0, 28.0))), TeamTemplate("2", "§a",listOf(SpawnPoint(80.0, -60.0, 8.0)))),
                    weapons = listOf("minecraft:wooden_sword", "minecraft:iron_sword", "minecraft:diamond_sword"),
                    maxPlayers = 10,
                    warmupTime = 10,
                    lobbyCoord = SpawnPoint(137.0, -54.0, 0.0),
                )
                val defaultList = listOf(defaultTemplate)

                // МАГИЯ 1: Превращаем наши объекты Котлина в текст формата JSON
                val jsonText = jsonFormat.encodeToString(defaultList)

                // Записываем этот текст в новый файл
                configFile.writeText(jsonText)

                // Сохраняем в память
                templates = defaultList

            } else {

                // --- РЕЖИМ ЧИТАТЕЛЯ (Файл уже есть) ---
                println("Чтение конфига ArmsRace...")

                // Читаем весь текст из файла
                val jsonText = configFile.readText()

                // МАГИЯ 2: Превращаем текст обратно в объекты Котлина
                templates = jsonFormat.decodeFromString(jsonText)

                println("Успешно загружено арен: ${templates.size}")
            }

        } catch (e: Exception) {
            // Если админ забыл поставить кавычку в JSON, мы поймаем ошибку здесь!
            println("КРИТИЧЕСКАЯ ОШИБКА В КОНФИГЕ ARMSRACE: ${e.message}")
            // Чтобы мод не сломался полностью, выдадим пустой список
            templates = emptyList()
        }
    }
}