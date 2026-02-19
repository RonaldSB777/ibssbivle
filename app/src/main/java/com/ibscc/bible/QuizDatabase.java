package com.ibscc.bible;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizDatabase {

    private List<Pergunta> perguntasFaceis;
    private List<Pergunta> perguntasMedias;
    private List<Pergunta> perguntasDificeis;
    private Random random;

    public QuizDatabase() {
        random = new Random();
        carregarPerguntas();
    }

    private void carregarPerguntas() {
        // NÍVEL FÁCIL (30 perguntas)
        perguntasFaceis = new ArrayList<>();
        perguntasFaceis.add(new Pergunta("Quem foi o primeiro homem criado por Deus?", 
            new String[]{"Adão", "Moisés", "Noé", "Abraão"}, 0, "facil", "Gênesis 2:7"));
        perguntasFaceis.add(new Pergunta("Quantos discípulos Jesus tinha?", 
            new String[]{"10", "12", "7", "70"}, 1, "facil", "Mateus 10:1"));
        perguntasFaceis.add(new Pergunta("Quem construiu a arca para salvar os animais do dilúvio?", 
            new String[]{"Moisés", "Noé", "Abraão", "Davi"}, 1, "facil", "Gênesis 6:13-22"));
        perguntasFaceis.add(new Pergunta("Qual o nome da mãe de Jesus?", 
            new String[]{"Marta", "Maria", "Madalena", "Elisabet"}, 1, "facil", "Lucas 1:31"));
        perguntasFaceis.add(new Pergunta("Quem matou o gigante Golias com uma pedra?", 
            new String[]{"Sansão", "Davi", "Josué", "Saul"}, 1, "facil", "1 Samuel 17:49"));
        perguntasFaceis.add(new Pergunta("Quantos dias Jesus jejuou no deserto?", 
            new String[]{"7 dias", "12 dias", "40 dias", "3 dias"}, 2, "facil", "Mateus 4:2"));
        perguntasFaceis.add(new Pergunta("Quem foi engolido por um grande peixe?", 
            new String[]{"Jonas", "Pedro", "Paulo", "João"}, 0, "facil", "Jonas 1:17"));
        perguntasFaceis.add(new Pergunta("Qual é o primeiro livro da Bíblia?", 
            new String[]{"Êxodo", "Gênesis", "Salmos", "Mateus"}, 1, "facil", ""));
        perguntasFaceis.add(new Pergunta("Quem recebeu os 10 Mandamentos no monte Sinai?", 
            new String[]{"Abraão", "Davi", "Moisés", "Josué"}, 2, "facil", "Êxodo 20"));
        perguntasFaceis.add(new Pergunta("Quantos apóstolos eram pescadores?", 
            new String[]{"2", "4", "7", "12"}, 1, "facil", "Mateus 4:18-22"));
        perguntasFaceis.add(new Pergunta("Quem traiu Jesus por 30 moedas de prata?", 
            new String[]{"Pedro", "Judas", "João", "Tiago"}, 1, "facil", "Mateus 26:15"));
        perguntasFaceis.add(new Pergunta("Qual animal falou com Balaão?", 
            new String[]{"Cavalo", "Camelo", "Jumenta", "Cobra"}, 2, "facil", "Números 22:28"));
        perguntasFaceis.add(new Pergunta("Quem era o irmão de Moisés?", 
            new String[]{"Arão", "Josué", "Calebe", "José"}, 0, "facil", "Êxodo 4:14"));
        perguntasFaceis.add(new Pergunta("O que Jesus multiplicou para alimentar 5 mil pessoas?", 
            new String[]{"Peixes e pães", "Carne e vinho", "Frutas", "Água"}, 0, "facil", "Mateus 14:19"));
        perguntasFaceis.add(new Pergunta("Quem foi lançado na cova dos leões?", 
            new String[]{"Davi", "Daniel", "José", "Jonas"}, 1, "facil", "Daniel 6:16"));
        perguntasFaceis.add(new Pergunta("Qual profeta foi levado ao céu num carro de fogo?", 
            new String[]{"Elias", "Eliseu", "Isaías", "Jeremias"}, 0, "facil", "2 Reis 2:11"));
        perguntasFaceis.add(new Pergunta("Quem é conhecido como o pai da fé?", 
            new String[]{"Moisés", "Davi", "Abraão", "Noé"}, 2, "facil", "Romanos 4:11"));
        perguntasFaceis.add(new Pergunta("Em qual cidade Jesus nasceu?", 
            new String[]{"Nazaré", "Jerusalém", "Belém", "Cafarnaum"}, 2, "facil", "Mateus 2:1"));
        perguntasFaceis.add(new Pergunta("Quem era a esposa de Abraão?", 
            new String[]{"Raquel", "Sara", "Rebeca", "Leia"}, 1, "facil", "Gênesis 17:15"));
        perguntasFaceis.add(new Pergunta("Qual o nome do jardim onde Adão e Eva moravam?", 
            new String[]{"Éden", "Getsemani", "Betel", "Sinai"}, 0, "facil", "Gênesis 2:15"));
        perguntasFaceis.add(new Pergunta("Quem foi vendido pelos irmãos como escravo no Egito?", 
            new String[]{"Benjamim", "José", "Rúben", "Judá"}, 1, "facil", "Gênesis 37:28"));
        perguntasFaceis.add(new Pergunta("Qual discípulo negou Jesus três vezes?", 
            new String[]{"João", "Pedro", "Tiago", "André"}, 1, "facil", "Mateus 26:75"));
        perguntasFaceis.add(new Pergunta("Quem batizou Jesus?", 
            new String[]{"João Batista", "Pedro", "Paulo", "Tiago"}, 0, "facil", "Mateus 3:13"));
        perguntasFaceis.add(new Pergunta("Qual é o último livro do Novo Testamento?", 
            new String[]{"Atos", "Apocalipse", "Romanos", "Hebreus"}, 1, "facil", ""));
        perguntasFaceis.add(new Pergunta("Quantos filhos Jacó teve?", 
            new String[]{"7", "10", "12", "14"}, 2, "facil", "Gênesis 35:22"));
        perguntasFaceis.add(new Pergunta("Quem era a prostituta que ajudou os espias em Jericó?", 
            new String[]{"Débora", "Raabe", "Rute", "Ester"}, 1, "facil", "Josué 2:1"));
        perguntasFaceis.add(new Pergunta("O que Jesus transformou em vinho?", 
            new String[]{"Sangue", "Água", "Leite", "Mel"}, 1, "facil", "João 2:9"));
        perguntasFaceis.add(new Pergunta("Quem curou o cego de nascença?", 
            new String[]{"Pedro", "Paulo", "Jesus", "João"}, 2, "facil", "João 9:1-7"));
        perguntasFaceis.add(new Pergunta("Qual o nome do anjo que anunciou o nascimento de Jesus a Maria?", 
            new String[]{"Miguel", "Gabriel", "Rafael", "Lucifer"}, 1, "facil", "Lucas 1:26"));
        perguntasFaceis.add(new Pergunta("Quem escreveu a maioria dos Salmos?", 
            new String[]{"Salomão", "Davi", "Moisés", "Daniel"}, 1, "facil", ""));

        // NÍVEL MÉDIO (25 perguntas)
        perguntasMedias = new ArrayList<>();
        perguntasMedias.add(new Pergunta("Quantos anos os israelitas andaram no deserto?", 
            new String[]{"7 anos", "12 anos", "40 anos", "70 anos"}, 2, "medio", "Números 14:33"));
        perguntasMedias.add(new Pergunta("Qual era o nome original de Paulo antes de se converter?", 
            new String[]{"Simão", "Saulo", "Tiago", "Estevão"}, 1, "medio", "Atos 13:9"));
        perguntasMedias.add(new Pergunta("Quantos livros tem a Bíblia no total?", 
            new String[]{"39", "27", "66", "73"}, 2, "medio", ""));
        perguntasMedias.add(new Pergunta("Quem foi a primeira pessoa a ver Jesus ressurreto?", 
            new String[]{"Maria Madalena", "Pedro", "João", "Maria mãe de Jesus"}, 0, "medio", "João 20:14"));
        perguntasMedias.add(new Pergunta("Qual era a profissão de Mateus antes de ser apóstolo?", 
            new String[]{"Pescador", "Cobrador de impostos", "Médico", "Carpinteiro"}, 1, "medio", "Mateus 9:9"));
        perguntasMedias.add(new Pergunta("Quantos anos viveu Matusalém?", 
            new String[]{"120 anos", "600 anos", "969 anos", "930 anos"}, 2, "medio", "Gênesis 5:27"));
        perguntasMedias.add(new Pergunta("Quem sucedeu Moisés como líder de Israel?", 
            new String[]{"Calebe", "Aarão", "Josué", "Gideão"}, 2, "medio", "Josué 1:1"));
        perguntasMedias.add(new Pergunta("Qual a menor tribo de Israel?", 
            new String[]{"Levi", "Benjamim", "Judá", "Efraim"}, 1, "medio", ""));
        perguntasMedias.add(new Pergunta("Quem interpretou os sonhos do Faraó no Egito?", 
            new String[]{"Moisés", "José", "Daniel", "José (NT)"}, 1, "medio", "Gênesis 41:15"));
        perguntasMedias.add(new Pergunta("Quantas vezes Pedro negou Jesus antes que o galo cantasse?", 
            new String[]{"Uma vez", "Duas vezes", "Três vezes", "Sete vezes"}, 2, "medio", "Mateus 26:34"));
        perguntasMedias.add(new Pergunta("Qual o nome do monte onde Moisés morreu?", 
            new String[]{"Monte Sinai", "Monte Nebo", "Monte Carmelo", "Monte das Oliveiras"}, 1, "medio", "Deuteronômio 34:1"));
        perguntasMedias.add(new Pergunta("Quem foi o primeiro mártir cristão?", 
            new String[]{"Pedro", "Paulo", "Estevão", "Tiago"}, 2, "medio", "Atos 7:59"));
        perguntasMedias.add(new Pergunta("Quantos discípulos viram Jesus na transfiguração?", 
            new String[]{"3", "6", "9", "12"}, 0, "medio", "Mateus 17:1"));
        perguntasMedias.add(new Pergunta("Qual era o nome da irmã de Moisés?", 
            new String[]{"Miriã", "Lea", "Raquel", "Débora"}, 0, "medio", "Êxodo 15:20"));
        perguntasMedias.add(new Pergunta("Quem foi o rei que construiu o templo de Jerusalém?", 
            new String[]{"Davi", "Salomão", "Ezequias", "Josias"}, 1, "medio", "1 Reis 6:1"));
        perguntasMedias.add(new Pergunta("Quantos filhos Davi teve em Jerusalém?", 
            new String[]{"4", "7", "13", "20"}, 2, "medio", "1 Crônicas 3:1-9"));
        perguntasMedias.add(new Pergunta("Qual era o nome do rio onde Jesus foi batizado?", 
            new String[]{"Rio Nilo", "Rio Jordão", "Rio Eufrates", "Mar da Galileia"}, 1, "medio", "Mateus 3:6"));
        perguntasMedias.add(new Pergunta("Quem escreveu Atos dos Apóstolos?", 
            new String[]{"Paulo", "Lucas", "João", "Pedro"}, 1, "medio", ""));
        perguntasMedias.add(new Pergunta("Quantos anos tinha Jesus quando começou seu ministério?", 
            new String[]{"12 anos", "25 anos", "30 anos", "33 anos"}, 2, "medio", "Lucas 3:23"));
        perguntasMedias.add(new Pergunta("Qual o nome da esposa de Aquila que trabalhou com Paulo?", 
            new String[]{"Lídia", "Priscila", "Eunice", "Loide"}, 1, "medio", "Atos 18:2"));
        perguntasMedias.add(new Pergunta("Quem perguntou a Jesus: 'Como posso nascer de novo sendo velho?'", 
            new String[]{"Nicodemos", "Zaqueu", "Natanael", "Gamaliel"}, 0, "medio", "João 3:4"));
        perguntasMedias.add(new Pergunta("Quantas pragas foram enviadas sobre o Egito?", 
            new String[]{"7", "10", "12", "3"}, 1, "medio", "Êxodo 7-12"));
        perguntasMedias.add(new Pergunta("Qual livro vem logo depois dos Evangelhos?", 
            new String[]{"Romanos", "Atos", "Apocalipse", "Hebreus"}, 1, "medio", ""));
        perguntasMedias.add(new Pergunta("Quem foi o pai de João Batista?", 
            new String[]{"Zacarias", "Eliseu", "Simeão", "José"}, 0, "medio", "Lucas 1:5"));
        perguntasMedias.add(new Pergunta("Quantas vezes caiu a muralha de Jericó?", 
            new String[]{"1 vez", "3 vezes", "7 vezes", "Não caiu"}, 2, "medio", "Josué 6:4"));

        // NÍVEL DIFÍCIL (20 perguntas)
        perguntasDificeis = new ArrayList<>();
        perguntasDificeis.add(new Pergunta("Quantos anos o povo de Israel ficou no cativeiro na Babilônia?", 
            new String[]{"40 anos", "70 anos", "120 anos", "430 anos"}, 1, "dificil", "Jeremias 25:11"));
        perguntasDificeis.add(new Pergunta("Qual era o nome da mãe de João Batista?", 
            new String[]{"Isabel", "Ana", "Maria", "Joana"}, 0, "dificil", "Lucas 1:5"));
        perguntasDificeis.add(new Pergunta("Quantos anos viveu Adão?", 
            new String[]{"120 anos", "930 anos", "800 anos", "1000 anos"}, 1, "dificil", "Gênesis 5:5"));
        perguntasDificeis.add(new Pergunta("Quem escreveu Eclesiastes?", 
            new String[]{"Davi", "Salomão", "Josias", "Ezequias"}, 1, "dificil", ""));
        perguntasDificeis.add(new Pergunta("Qual era o nome do servo de Abraão que buscou esposa para Isaque?", 
            new String[]{"Eliezer", "Labão", "Betoel", "Naor"}, 0, "dificil", "Gênesis 24:2"));
        perguntasDificeis.add(new Pergunta("Quantas vezes Paulo foi azorrado?", 
            new String[]{"Uma vez", "Três vezes", "Cinco vezes", "Nove vezes"}, 2, "dificil", "2 Coríntios 11:24"));
        perguntasDificeis.add(new Pergunta("Qual era o nome do anjo que lutou com Jacó?", 
            new String[]{"Miguel", "Não é revelado", "Gabriel", "Rafael"}, 1, "dificil", "Gênesis 32:24"));
        perguntasDificeis.add(new Pergunta("Quantos anos tinha Ezequias quando começou a reinar?", 
            new String[]{"8 anos", "12 anos", "25 anos", "30 anos"}, 2, "dificil", "2 Reis 18:2"));
        perguntasDificeis.add(new Pergunta("Quem era o sumo sacerdote quando Jesus foi crucificado?", 
            new String[]{"Anás", "Caifás", "Ambos", "Gamaliel"}, 2, "dificil", "Lucas 3:2"));
        perguntasDificeis.add(new Pergunta("Quantas vezes Naamã mergulhou no Jordão para ser curado?", 
            new String[]{"Uma vez", "Três vezes", "Sete vezes", "Doze vezes"}, 2, "dificil", "2 Reis 5:14"));
        perguntasDificeis.add(new Pergunta("Qual era o nome da avó de Timóteo?", 
            new String[]{"Loide", "Eunice", "Ana", "Isabel"}, 0, "dificil", "2 Timóteo 1:5"));
        perguntasDificeis.add(new Pergunta("Quantos anos reinou Davi em Jerusalém?", 
            new String[]{"7 anos", "33 anos", "40 anos", "70 anos"}, 2, "dificil", "2 Samuel 5:5"));
        perguntasDificeis.add(new Pergunta("Quem profetizou que Jesus nasceria em Belém?", 
            new String[]{"Isaías", "Miquéias", "Jeremias", "Ezequiel"}, 1, "dificil", "Miquéias 5:2"));
        perguntasDificeis.add(new Pergunta("Qual era o nome do coveiro que enterrou Saul?", 
            new String[]{"Ripa", "Abner", "Jabes", "Baasa"}, 0, "dificil", "2 Samuel 21:11"));
        perguntasDificeis.add(new Pergunta("Quantos capítulos tem o livro de Salmos?", 
            new String[]{"100", "119", "150", "180"}, 2, "dificil", ""));
        perguntasDificeis.add(new Pergunta("Quem foi o primeiro gentio a ser batizado?", 
            new String[]{"Cornélio", "Lídia", "O eunuco etíope", "Paulo"}, 0, "dificil", "Atos 10:48"));
        perguntasDificeis.add(new Pergunta("Qual era o nome do pai de Zaqueu?", 
            new String[]{"José", "Matarias", "Aleu", "Jairo"}, 2, "dificil", "Lucas 19:2"));
        perguntasDificeis.add(new Pergunta("Quantos anos serviu Jacó por Raquel?", 
            new String[]{"7 anos", "10 anos", "14 anos", "20 anos"}, 2, "dificil", "Gênesis 29:20"));
        perguntasDificeis.add(new Pergunta("Qual era o nome da montanha onde Abraão foi sacrificar Isaque?", 
            new String[]{"Moria", "Gerezim", "Ebal", "Nebo"}, 0, "dificil", "Gênesis 22:2"));
        perguntasDificeis.add(new Pergunta("Quantas pessoas foram salvas na arca de Noé?", 
            new String[]{"4", "6", "8", "12"}, 2, "dificil", "1 Pedro 3:20"));
    }

    public List<Pergunta> getPerguntasPorNivel(String nivel, int quantidade) {
        List<Pergunta> lista;
        
        switch(nivel) {
            case "facil":
                lista = new ArrayList<>(perguntasFaceis);
                break;
            case "medio":
                lista = new ArrayList<>(perguntasMedias);
                break;
            case "dificil":
                lista = new ArrayList<>(perguntasDificeis);
                break;
            default:
                lista = new ArrayList<>(perguntasFaceis);
        }
        
        // Embaralhar e retornar a quantidade solicitada
        Collections.shuffle(lista, random);
        
        if (quantidade > lista.size()) {
            quantidade = lista.size();
        }
        
        return lista.subList(0, quantidade);
    }
}