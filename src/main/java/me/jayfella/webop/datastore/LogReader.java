/*
 * Copyright (c) 2020 - @FabioZumbi12
 * Last Modified: 14/06/2020 00:14.
 *
 * This class is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any
 *  damages arising from the use of this class.
 *
 * Permission is granted to anyone to use this class for any purpose, including commercial plugins, and to alter it and
 * redistribute it freely, subject to the following restrictions:
 * 1 - The origin of this class must not be misrepresented; you must not claim that you wrote the original software. If you
 * use this class in other plugins, an acknowledgment in the plugin documentation would be appreciated but is not required.
 * 2 - Altered source versions must be plainly marked as such, and must not be misrepresented as being the original class.
 * 3 - This notice may not be removed or altered from any source distribution.
 *
 * Esta classe é fornecida "como está", sem qualquer garantia expressa ou implícita. Em nenhum caso os autores serão
 * responsabilizados por quaisquer danos decorrentes do uso desta classe.
 *
 * É concedida permissão a qualquer pessoa para usar esta classe para qualquer finalidade, incluindo plugins pagos, e para
 * alterá-lo e redistribuí-lo livremente, sujeito às seguintes restrições:
 * 1 - A origem desta classe não deve ser deturpada; você não deve afirmar que escreveu a classe original. Se você usar esta
 *  classe em um plugin, uma confirmação de autoria na documentação do plugin será apreciada, mas não é necessária.
 * 2 - Versões de origem alteradas devem ser claramente marcadas como tal e não devem ser deturpadas como sendo a
 * classe original.
 * 3 - Este aviso não pode ser removido ou alterado de qualquer distribuição de origem.
 */

package me.jayfella.webop.datastore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LogReader {
    public String searchLog(final String searchTerm) {
        if (searchTerm.length() < 3) {
            return "Search term must be greater than 3 characters.";
        }
        List<String> results = new ArrayList<>();
        try {
            try (Scanner scan = new Scanner(new FileInputStream(new File("logs/latest.log")))) {
                while (scan.hasNext()) {
                    String line = scan.nextLine();

                    if (line.toLowerCase().contains(searchTerm.toLowerCase())) {
                        results.add(line);
                    }
                }
            }
        } catch (FileNotFoundException ignored) {
        }

        StringBuilder response = new StringBuilder();

        if (results.isEmpty()) {
            results.add("No results found!");
        }

        for (String result : results) {
            response.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>&bull;</strong>&nbsp;").append(result).append("<br/>");
        }
        return response.toString();
    }
}
